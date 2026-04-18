package com.example.ultra.core.domain.model

data class Cart(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "EUR"
) {
    val itemCount: Int get() = items.sumOf { it.quantity }
    val cartTotal: Double get() = items.sumOf { it.subtotal }
    val formattedTotal: String get() = "$currency ${"%.2f".format(cartTotal)}"
    val formattedSubtotal: String get() = "$currency ${"%.2f".format(subtotal)}"
    val isEmpty: Boolean get() = items.isEmpty()
}