package com.example.ultra.local_market.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalVendor(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val location: String,
    val rating: Double = 0.0,
    val isOpen: Boolean = true
)

@Serializable
data class LocalProduct(
    val id: String,
    val vendorId: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "NGN",
    val imageUrl: String = "",
    val category: String = "",
    val inStock: Boolean = true
)

@Serializable
data class LocalMarketOrder(
    val id: String,
    val vendorId: String,
    val vendorName: String,
    val items: List<LocalMarketOrderItem>,
    val totalAmount: Double,
    val currency: String,
    val status: String,
    val createdAt: String
)

@Serializable
data class LocalMarketOrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
)
