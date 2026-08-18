package com.example.ultra.core.domain.model

data class Order(
    val id: String,
    val status: String,
    val deliveryMethod: String = "Shipping",
    val pickupStationName: String? = null,
    val subtotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "EUR",
    val items: List<OrderItem> = emptyList(),
    val shippingAddress: Address? = null,
    val createdAt: String = ""
) {
    val isPaid: Boolean get() = status.equals("Paid", ignoreCase = true)
    val isPending: Boolean get() = status.equals("Pending", ignoreCase = true)
}

data class OrderItem(
    val id: String,
    val productId: String,
    val variantId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val lineTotal: Double,
    val currency: String,
    val thumbnailUrl: String? = null
)

data class Address(
    val firstName: String,
    val lastName: String,
    val line1: String,
    val line2: String? = null,
    val city: String,
    val state: String? = null,
    val postalCode: String,
    val country: String,
    val phone: String? = null
)
