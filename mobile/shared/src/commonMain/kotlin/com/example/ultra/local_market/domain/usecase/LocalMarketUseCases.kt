package com.example.ultra.local_market.domain.usecase

import com.example.ultra.local_market.data.repository.LocalMarketRepository

data class LocalMarketUseCases(
    val getVendors: GetLocalVendorsUseCase,
    val getProducts: GetLocalProductsUseCase,
    val placeOrder: PlaceLocalMarketOrderUseCase,
    val getOrders: GetLocalMarketOrdersUseCase
)

class GetLocalVendorsUseCase(private val repo: LocalMarketRepository) {
    suspend operator fun invoke() = repo.getVendors()
}

class GetLocalProductsUseCase(private val repo: LocalMarketRepository) {
    suspend operator fun invoke(vendorId: String) = repo.getProducts(vendorId)
}

class PlaceLocalMarketOrderUseCase(private val repo: LocalMarketRepository) {
    suspend operator fun invoke(vendorId: String, items: List<Pair<String, Int>>) =
        repo.placeOrder(vendorId, items)
}

class GetLocalMarketOrdersUseCase(private val repo: LocalMarketRepository) {
    suspend operator fun invoke() = repo.getOrders()
}
