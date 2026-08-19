package com.example.ultra.shopping.checkout.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.shopping.cart.domain.usecase.ClearCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.GetCartUseCase
import com.example.ultra.shopping.checkout.domain.usecase.CheckoutUseCase
import com.example.ultra.shopping.checkout.domain.usecase.InitiatePaymentUseCase
import com.example.ultra.shopping.checkout.domain.usecase.VerifyPaymentUseCase
import com.example.ultra.shopping.checkout.presentation.intent.CheckoutAction
import com.example.ultra.shopping.checkout.presentation.intent.CheckoutEvent
import com.example.ultra.shopping.checkout.presentation.intent.CheckoutState
import com.example.ultra.shopping.checkout.presentation.intent.CheckoutStep
import com.example.ultra.shopping.checkout.presentation.intent.DeliveryMethod
import com.example.ultra.core.data.CheckoutInfo
import com.example.ultra.core.data.CheckoutItemDto
import com.example.ultra.core.data.LocalCheckoutStorage
import com.example.ultra.core.domain.model.Address
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.UiText
import com.example.ultra.core.presentation.toUiText
import com.example.ultra.shopping.home.domain.usecase.GetPickupStationsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val initiatePaymentUseCase: InitiatePaymentUseCase,
    private val verifyPaymentUseCase: VerifyPaymentUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val authRepository: AuthRepository,
    private val getPickupStationsUseCase: GetPickupStationsUseCase,
    private val localCheckoutStorage: LocalCheckoutStorage
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    private val _events = Channel<CheckoutEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val isAuthenticated get() = authRepository.getCurrentUser() != null

    init {
        prefilledFromUser()
        loadCheckoutInfo()
        loadCart()
        loadPickupStations()
    }

    fun onAction(action: CheckoutAction) {
        when (action) {
            is CheckoutAction.OnFirstNameChange -> _state.update { it.copy(firstName = action.value) }
            is CheckoutAction.OnLastNameChange -> _state.update { it.copy(lastName = action.value) }
            is CheckoutAction.OnEmailChange -> _state.update { it.copy(email = action.value) }
            is CheckoutAction.OnLine1Change -> _state.update { it.copy(line1 = action.value) }
            is CheckoutAction.OnCityChange -> _state.update { it.copy(city = action.value) }
            is CheckoutAction.OnPostalCodeChange -> _state.update { it.copy(postalCode = action.value) }
            is CheckoutAction.OnCountryChange -> _state.update { it.copy(country = action.value.uppercase()) }
            is CheckoutAction.OnPhoneChange -> _state.update { it.copy(phone = action.value) }
            is CheckoutAction.SelectTab -> _state.update { it.copy(step = action.step) }
            is CheckoutAction.SelectDeliveryMethod -> {
                _state.update { it.copy(deliveryMethod = action.method) }
                recalculateFees()
            }
            is CheckoutAction.SelectPickupStation -> _state.update { it.copy(selectedStation = action.station) }
            is CheckoutAction.PlaceOrder -> placeOrder()
            is CheckoutAction.StartPayment -> startPayment()
            is CheckoutAction.ConfirmPayment -> confirmPayment()
            is CheckoutAction.BackToAddress -> _state.update { it.copy(step = CheckoutStep.Delivery, error = null) }
        }
    }

    private fun prefilledFromUser() {
        val user = authRepository.getCurrentUser()
        _state.update { it.copy(isAuthenticated = user != null) }
        if (user != null) {
            _state.update {
                it.copy(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    phone = user.phone ?: ""
                )
            }
        }
    }

    private fun loadCheckoutInfo() {
        // For guests, load previously saved checkout info from local storage
        if (isAuthenticated) return
        val saved = localCheckoutStorage.getCheckoutInfo()
        if (saved.firstName.isBlank() && saved.email.isBlank()) return
        _state.update {
            it.copy(
                firstName = saved.firstName,
                lastName = saved.lastName,
                email = saved.email,
                line1 = saved.line1,
                city = saved.city,
                postalCode = saved.postalCode,
                country = saved.country,
                phone = saved.phone
            )
        }
    }

    private fun saveCheckoutInfo() {
        if (isAuthenticated) return
        val s = _state.value
        localCheckoutStorage.saveCheckoutInfo(
            CheckoutInfo(
                firstName = s.firstName.trim(),
                lastName = s.lastName.trim(),
                email = s.email.trim(),
                line1 = s.line1.trim(),
                city = s.city.trim(),
                postalCode = s.postalCode.trim(),
                country = s.country.trim(),
                phone = s.phone.trim()
            )
        )
    }

    private fun loadCart() {
        viewModelScope.launch {
            getCartUseCase().onSuccess { cart ->
                _state.update {
                    it.copy(
                        cartTotal = cart.total,
                        currency = cart.currency,
                        cartItems = cart.items
                    )
                }
                recalculateFees()
            }
        }
    }

    private fun loadPickupStations() {
        viewModelScope.launch {
            getPickupStationsUseCase().onSuccess { stations ->
                _state.update { it.copy(pickupStations = stations) }
            }
        }
    }

    private fun recalculateFees() {
        val cartTotal = _state.value.cartTotal
        val method = _state.value.deliveryMethod
        val shippingFee = if (method == DeliveryMethod.Shipping) cartTotal * 0.05 else 0.0
        val pickupFee = if (method == DeliveryMethod.Pickup) cartTotal * 0.02 else 0.0
        _state.update { it.copy(shippingFee = shippingFee, pickupFee = pickupFee) }
    }

    private fun placeOrder() {
        val s = _state.value
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.line1.isBlank() ||
            s.city.isBlank() || s.postalCode.isBlank() || s.country.length != 2
        ) {
            _state.update { it.copy(error = UiText.DynamicString("Please complete all address fields (country as a 2-letter code).")) }
            return
        }

        if (s.deliveryMethod == DeliveryMethod.Pickup && s.selectedStation == null) {
            _state.update { it.copy(error = UiText.DynamicString("Please select a pickup station.")) }
            return
        }

        // Email is required for guest checkout
        if (!isAuthenticated && s.email.isBlank()) {
            _state.update { it.copy(error = UiText.DynamicString("Email is required for checkout.")) }
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

            // Persist checkout info for guests so next time is pre-filled
            saveCheckoutInfo()

            val method = if (s.deliveryMethod == DeliveryMethod.Pickup) "Pickup" else "Shipping"
            val stationId = s.selectedStation?.id

            // For guests, send cart items + email; for authenticated users, server uses their cart
            val items = if (!isAuthenticated) {
                s.cartItems.map { CheckoutItemDto(variantId = it.variantId, quantity = it.quantity) }
            } else null
            val email = if (!isAuthenticated) s.email.trim() else null

            checkoutUseCase(address, method, stationId, items, email)
                .onSuccess { order ->
                    println("success ${order.id}")
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
        println("startPayment ${_state.value.order?.id}")
        val order = _state.value.order ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            initiatePaymentUseCase(order.id)
                .onSuccess { init ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            paymentInitiation = init,
                            step = CheckoutStep.Payment
                        )
                    }
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
                if (attempt < MAX_VERIFY_ATTEMPTS - 1) delay(VERIFY_DELAY_MS.milliseconds)
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
