package com.example.ultra.cart.presentation.intent

import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.presentation.UiText

data class CartState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val error: UiText? = null
)

sealed interface CartAction {
    data object LoadCart : CartAction
    data class UpdateQuantity(val cartItemId: String, val quantity: Int) : CartAction
    data class RemoveItem(val cartItemId: String) : CartAction
    data object ClearCart : CartAction
}

sealed interface CartEvent {
    data class ShowError(val message: UiText) : CartEvent
}
