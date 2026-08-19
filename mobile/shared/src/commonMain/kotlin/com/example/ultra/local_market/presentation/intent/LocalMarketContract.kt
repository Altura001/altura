package com.example.ultra.local_market.presentation.intent

import com.example.ultra.core.presentation.UiText
import com.example.ultra.local_market.domain.model.LocalMarketOrder
import com.example.ultra.local_market.domain.model.LocalProduct
import com.example.ultra.local_market.domain.model.LocalVendor

data class LocalMarketState(
    val isLoading: Boolean = false,
    val vendors: List<LocalVendor> = emptyList(),
    val selectedVendor: LocalVendor? = null,
    val products: List<LocalProduct> = emptyList(),
    val orders: List<LocalMarketOrder> = emptyList(),
    val error: UiText? = null
)

sealed interface LocalMarketAction {
    data object LoadVendors : LocalMarketAction
    data class SelectVendor(val vendor: LocalVendor) : LocalMarketAction
    data class PlaceOrder(val vendorId: String, val items: List<Pair<String, Int>>) : LocalMarketAction
    data object LoadOrders : LocalMarketAction
    data object ClearError : LocalMarketAction
}

sealed interface LocalMarketEvent {
    data class ShowError(val message: UiText) : LocalMarketEvent
    data object OrderPlaced : LocalMarketEvent
}
