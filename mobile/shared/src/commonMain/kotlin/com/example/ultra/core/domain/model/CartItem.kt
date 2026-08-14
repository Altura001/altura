package com.example.ultra.core.domain.model

data class CartItem(
    val id: String,
    val productId: String,
    val variantId: String = "",
    val title: String = "",
    val product: Product? = null,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "EUR",
    val imageUrl: String? = null
) {
    val subtotal: Double get() = unitPrice * quantity

    fun toProduct(): Product {
        return product ?: Product(
            id = productId,
            vendorId = "",
            name = title,
            description = "",
            price = unitPrice,
            currency = currency,
            imageUrl = imageUrl
        )
    }
}