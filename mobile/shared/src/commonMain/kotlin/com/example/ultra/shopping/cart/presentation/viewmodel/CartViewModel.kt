package com.example.ultra.shopping.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.shopping.cart.domain.usecase.ClearCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.GetCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.RemoveFromCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.UpdateCartItemUseCase
import com.example.ultra.shopping.cart.presentation.intent.CartAction
import com.example.ultra.shopping.cart.presentation.intent.CartState
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.core.presentation.toUiText
import com.example.ultra.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val notificationManager: NotificationManager,
    cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.observeCart().collect { cart ->
                _state.update { it.copy(cart = cart) }
            }
        }
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
                    notificationManager.error(error.toUiText().asString())
                }
        }
    }

    private fun updateQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            updateCartItemUseCase(cartItemId, quantity)
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }

    private fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            removeFromCartUseCase(cartItemId)
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }
}
