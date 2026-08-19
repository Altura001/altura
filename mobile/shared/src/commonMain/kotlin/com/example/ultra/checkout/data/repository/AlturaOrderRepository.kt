package com.example.ultra.checkout.data.repository

import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.data.CheckoutItemDto
import com.example.ultra.core.data.toDto
import com.example.ultra.core.data.toOrder
import com.example.ultra.core.data.util.safeApiCall
import com.example.ultra.core.domain.model.Address
import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.repository.OrderRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.core.domain.util.map

/** OrderRepository backed by the Altura Nova .NET backend. */
class AlturaOrderRepository(
    private val api: AlturaApiService
) : OrderRepository {

    override suspend fun checkout(address: Address, deliveryMethod: String?, pickupStationId: String?, items: List<CheckoutItemDto>?, email: String?): Result<Order, DataError.Network> =
        safeApiCall { api.checkout(address.toDto(), deliveryMethod, pickupStationId, items, email).toOrder() }

    override suspend fun getOrders(): Result<List<Order>, DataError.Network> =
        safeApiCall { api.getOrders() }.map { list -> list.items.map { it.toOrder() } }

    override suspend fun getOrder(orderId: String): Result<Order, DataError.Network> =
        safeApiCall { api.getOrder(orderId).toOrder() }

    override suspend fun cancelOrder(orderId: String): Result<Order, DataError.Network> =
        safeApiCall { api.cancelOrder(orderId).toOrder() }
}
