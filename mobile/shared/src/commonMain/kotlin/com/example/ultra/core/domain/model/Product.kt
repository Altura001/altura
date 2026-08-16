package com.example.ultra.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val vendorId: String,
    val name: String,
    val description: String,
    val price: Double,
    val oldPrice: Double? = null,
    val currency: String = "NGN",
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val category: String? = null,
    val inStock: Boolean = true,
    val variants: List<ProductVariant> = emptyList(),
    val handle: String = ""
)
@Serializable
data class ProductVariant(
    val id: String,
    val title: String,
    val sku: String? = null,
    val price: Double = 0.0,
    val currency: String = "EUR",
    val inventoryQuantity: Int = 0
) {
    val isInStock: Boolean get() = inventoryQuantity > 0
}