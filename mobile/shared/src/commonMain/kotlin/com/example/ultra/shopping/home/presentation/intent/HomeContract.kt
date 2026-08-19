package com.example.ultra.shopping.home.presentation.intent

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.presentation.UiText

data class HomeState(
    val isLoading: Boolean = false,
    val vendors: List<Vendor> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedVendor: Vendor? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val error: UiText? = null
)

sealed interface HomeAction {
    data object LoadVendors : HomeAction
    data object LoadAllProducts : HomeAction
    data class SelectVendor(val vendorId: String) : HomeAction
    data object ClearSelection : HomeAction
    data object ClearError : HomeAction
    data class AddToCart(val product: Product) : HomeAction
    data class Search(val query: String) : HomeAction
    data object GoBack : HomeAction
}

sealed interface HomeEvent {
    data class ShowError(val message: UiText) : HomeEvent
}
