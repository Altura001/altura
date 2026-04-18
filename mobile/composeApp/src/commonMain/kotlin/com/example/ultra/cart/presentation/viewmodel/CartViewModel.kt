package com.example.ultra.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.cart.domain.usecase.GetCartUseCase
import com.example.ultra.cart.domain.usecase.RemoveFromCartUseCase
import com.example.ultra.cart.domain.usecase.UpdateCartItemUseCase
import com.example.ultra.cart.presentation.intent.CartIntent
import com.example.ultra.cart.presentation.intent.CartState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()
    
    init {
        onAction(CartIntent.LoadCart)
    }
    
    fun onAction(action: CartIntent) {
        when (action) {
            is CartIntent.LoadCart -> loadCart()
            is CartIntent.UpdateQuantity -> updateQuantity(action.cartItemId, action.quantity)
            is CartIntent.RemoveItem -> removeItem(action.cartItemId)
            is CartIntent.ClearCart -> clearCart()
            is CartIntent.ClearError -> clearError()
        }
    }
    
    private fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val cart = getCartUseCase()
                _state.update { it.copy(isLoading = false, cart = cart) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun updateQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val cart = updateCartItemUseCase(cartItemId, quantity)
                _state.update { it.copy(isLoading = false, cart = cart) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val cart = removeFromCartUseCase(cartItemId)
                _state.update { it.copy(isLoading = false, cart = cart) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun clearCart() {
        _state.update { it.copy(isLoading = true, cart = it.cart?.copy(items = emptyList())) }
    }
    
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
