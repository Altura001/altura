package com.example.ultra.home.presentation.productdetail

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.presentation.UiText

data class ProductDetailState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: UiText? = null,
    val cartQuantity: Int = 0,
    val isWishlisted: Boolean = false
)

sealed interface ProductDetailAction {
    data class LoadProduct(val handle: String) : ProductDetailAction
    data class AddToCart(val product: Product) : ProductDetailAction
    data class IncrementQuantity(val product: Product) : ProductDetailAction
    data class DecrementQuantity(val product: Product) : ProductDetailAction
    data class ToggleWishlist(val product: Product) : ProductDetailAction
}
