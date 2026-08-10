package com.example.ultra.catalog.presentation.productdetail

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.presentation.UiText

data class ProductDetailState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: UiText? = null,
    val addedToCart: Boolean = false
)

sealed interface ProductDetailAction {
    data class LoadProduct(val handle: String) : ProductDetailAction
    data class AddToCart(val product: Product) : ProductDetailAction
}

sealed interface ProductDetailEvent {
    data class ShowError(val message: UiText) : ProductDetailEvent
}
