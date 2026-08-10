package com.example.ultra.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.cart.domain.usecase.ClearCartUseCase
import com.example.ultra.cart.domain.usecase.GetCartUseCase
import com.example.ultra.cart.domain.usecase.RemoveFromCartUseCase
import com.example.ultra.cart.domain.usecase.UpdateCartItemUseCase
import com.example.ultra.cart.presentation.intent.CartAction
import com.example.ultra.cart.presentation.intent.CartEvent
import com.example.ultra.cart.presentation.intent.CartState
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    private val _events = Channel<CartEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        onAction(CartAction.LoadCart)
    }

    fun onAction(action: CartAction) {
        when (action) {
            is CartAction.LoadCart -> loadCart()
            is CartAction.UpdateQuantity -> updateQuantity(action.cartItemId, action.quantity)
            is CartAction.RemoveItem -> removeItem(action.cartItemId)
            is CartAction.ClearCart -> clearCart()
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getCartUseCase()
                .onSuccess { cart -> _state.update { it.copy(isLoading = false, cart = cart) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                    _events.send(CartEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun updateQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateCartItemUseCase(cartItemId, quantity)
                .onSuccess { cart -> _state.update { it.copy(isLoading = false, cart = cart) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CartEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            removeFromCartUseCase(cartItemId)
                .onSuccess { cart -> _state.update { it.copy(isLoading = false, cart = cart) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CartEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            clearCartUseCase()
                .onSuccess { cart -> _state.update { it.copy(isLoading = false, cart = cart) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CartEvent.ShowError(error.toUiText()))
                }
        }
    }
}
