package com.example.ultra.food.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Double = 0.0,
    val deliveryTimeMinutes: Int = 0,
    val isOpen: Boolean = true,
    val address: String = ""
)

@Serializable
data class MenuItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "NGN",
    val imageUrl: String = "",
    val category: String = "",
    val isAvailable: Boolean = true
)

@Serializable
data class FoodCart(
    val items: List<FoodCartItem> = emptyList(),
    val restaurantId: String? = null,
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "NGN"
)

@Serializable
data class FoodCartItem(
    val menuItem: MenuItem,
    val quantity: Int = 1
) {
    val lineTotal: Double get() = menuItem.price * quantity
}

@Serializable
data class FoodOrder(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<FoodCartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val currency: String,
    val status: String,
    val createdAt: String
)
