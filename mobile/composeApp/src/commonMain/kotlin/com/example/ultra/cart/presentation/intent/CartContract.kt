package com.example.ultra.cart.presentation.intent

import com.example.ultra.core.domain.model.Cart

data class CartState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val error: String? = null
)

sealed interface CartIntent {
    data object LoadCart : CartIntent
    data class UpdateQuantity(val cartItemId: String, val quantity: Int) : CartIntent
    data class RemoveItem(val cartItemId: String) : CartIntent
    data object ClearCart : CartIntent
    data object ClearError : CartIntent
}
