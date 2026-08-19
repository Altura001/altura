package com.example.ultra.local_market.data.repository

import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.local_market.domain.model.LocalMarketOrder
import com.example.ultra.local_market.domain.model.LocalProduct
import com.example.ultra.local_market.domain.model.LocalVendor

interface LocalMarketRepository {
    suspend fun getVendors(): Result<List<LocalVendor>, DataError.Network>
    suspend fun getProducts(vendorId: String): Result<List<LocalProduct>, DataError.Network>
    suspend fun placeOrder(vendorId: String, items: List<Pair<String, Int>>): Result<LocalMarketOrder, DataError.Network>
    suspend fun getOrders(): Result<List<LocalMarketOrder>, DataError.Network>
}
