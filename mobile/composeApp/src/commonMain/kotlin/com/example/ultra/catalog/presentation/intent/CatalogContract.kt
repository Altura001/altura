package com.example.ultra.catalog.presentation.intent

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor

data class CatalogState(
    val isLoading: Boolean = false,
    val vendors: List<Vendor> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedVendor: Vendor? = null,
    val error: String? = null
)

sealed interface CatalogIntent {
    data object LoadVendors : CatalogIntent
    data object LoadAllProducts : CatalogIntent
    data class SelectVendor(val vendorId: String) : CatalogIntent
    data object ClearSelection : CatalogIntent
    data object ClearError : CatalogIntent
}
