package com.example.ultra.wishlist.presentation.intent

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.presentation.UiText

data class WishlistState(
    val isLoading: Boolean = false,
    val items: List<Product> = emptyList(),
    val error: UiText? = null
)

sealed interface WishlistAction {
    data object LoadWishlist : WishlistAction
    data class RemoveFromWishlist(val productId: String) : WishlistAction
    data class AddToCart(val product: Product) : WishlistAction
    data object GoBack : WishlistAction
}
