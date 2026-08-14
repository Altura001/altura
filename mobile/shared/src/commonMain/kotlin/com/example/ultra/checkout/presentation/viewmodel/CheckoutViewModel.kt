package com.example.ultra.checkout.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.cart.domain.usecase.ClearCartUseCase
import com.example.ultra.cart.domain.usecase.GetCartUseCase
import com.example.ultra.checkout.domain.usecase.CheckoutUseCase
import com.example.ultra.checkout.domain.usecase.InitiatePaymentUseCase
import com.example.ultra.checkout.domain.usecase.VerifyPaymentUseCase
import com.example.ultra.checkout.presentation.intent.CheckoutAction
import com.example.ultra.checkout.presentation.intent.CheckoutEvent
import com.example.ultra.checkout.presentation.intent.CheckoutState
import com.example.ultra.checkout.presentation.intent.CheckoutStep
import com.example.ultra.core.domain.model.Address
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.UiText
import com.example.ultra.core.presentation.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val initiatePaymentUseCase: InitiatePaymentUseCase,
    private val verifyPaymentUseCase: VerifyPaymentUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    private val _events = Channel<CheckoutEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadCartSummary()
    }

    fun onAction(action: CheckoutAction) {
        when (action) {
            is CheckoutAction.OnFirstNameChange -> _state.update { it.copy(firstName = action.value) }
            is CheckoutAction.OnLastNameChange -> _state.update { it.copy(lastName = action.value) }
            is CheckoutAction.OnLine1Change -> _state.update { it.copy(line1 = action.value) }
            is CheckoutAction.OnCityChange -> _state.update { it.copy(city = action.value) }
            is CheckoutAction.OnPostalCodeChange -> _state.update { it.copy(postalCode = action.value) }
            is CheckoutAction.OnCountryChange -> _state.update { it.copy(country = action.value.uppercase()) }
            is CheckoutAction.OnPhoneChange -> _state.update { it.copy(phone = action.value) }
            is CheckoutAction.PlaceOrder -> placeOrder()
            is CheckoutAction.StartPayment -> startPayment()
            is CheckoutAction.ConfirmPayment -> confirmPayment()
            is CheckoutAction.BackToAddress -> _state.update { it.copy(step = CheckoutStep.Address, error = null) }
        }
    }

    private fun loadCartSummary() {
        viewModelScope.launch {
            getCartUseCase().onSuccess { cart ->
                _state.update { it.copy(cartTotal = cart.total, currency = cart.currency) }
            }
        }
    }

    private fun placeOrder() {
        val s = _state.value
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.line1.isBlank() ||
            s.city.isBlank() || s.postalCode.isBlank() || s.country.length != 2
        ) {
            _state.update { it.copy(error = UiText.DynamicString("Please complete all address fields (country as a 2-letter code).")) }
            return
        }

        val address = Address(
            firstName = s.firstName.trim(),
            lastName = s.lastName.trim(),
            line1 = s.line1.trim(),
            city = s.city.trim(),
            postalCode = s.postalCode.trim(),
            country = s.country.trim(),
            phone = s.phone.ifBlank { null }
        )

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            checkoutUseCase(address)
                .onSuccess { order ->
                    _state.update {
                        it.copy(isLoading = false, order = order, step = CheckoutStep.Payment)
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CheckoutEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun startPayment() {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            initiatePaymentUseCase(order.id)
                .onSuccess { init ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            paymentInitiation = init,
                            step = CheckoutStep.AwaitingConfirmation
                        )
                    }
                    _events.send(CheckoutEvent.OpenUrl(init.authorizationUrl))
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CheckoutEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun confirmPayment() {
        val order = _state.value.order ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Poll verification a few times to allow the provider callback to settle.
            repeat(MAX_VERIFY_ATTEMPTS) { attempt ->
                val result = verifyPaymentUseCase(order.id)
                var paid = false
                result.onSuccess { updated ->
                    if (updated.isPaid) {
                        paid = true
                        clearCartUseCase()
                        _state.update { it.copy(isLoading = false, order = updated, step = CheckoutStep.Success) }
                    }
                }
                if (paid) return@launch
                if (attempt < MAX_VERIFY_ATTEMPTS - 1) delay(VERIFY_DELAY_MS)
            }

            _state.update { it.copy(isLoading = false) }
            _events.send(
                CheckoutEvent.ShowError(
                    UiText.DynamicString("We couldn't confirm your payment yet. If you completed it, try again in a moment.")
                )
            )
        }
    }

    private companion object {
        const val MAX_VERIFY_ATTEMPTS = 4
        const val VERIFY_DELAY_MS = 2000L
    }
}
