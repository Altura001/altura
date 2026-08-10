package com.example.ultra.catalog.presentation.intent

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.presentation.UiText

data class CatalogState(
    val isLoading: Boolean = false,
    val vendors: List<Vendor> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedVendor: Vendor? = null,
    val error: UiText? = null
)

sealed interface CatalogAction {
    data object LoadVendors : CatalogAction
    data object LoadAllProducts : CatalogAction
    data class SelectVendor(val vendorId: String) : CatalogAction
    data object ClearSelection : CatalogAction
    data object ClearError : CatalogAction
    data class AddToCart(val product: Product) : CatalogAction
}

sealed interface CatalogEvent {
    data class ShowError(val message: UiText) : CatalogEvent
}
