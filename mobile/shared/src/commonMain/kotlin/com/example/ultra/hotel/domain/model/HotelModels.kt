package com.example.ultra.hotel.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val type: String,
    val pricePerNight: Double,
    val currency: String = "NGN",
    val rating: Double = 0.0,
    val address: String = "",
    val amenities: List<String> = emptyList(),
    val isAvailable: Boolean = true
)

@Serializable
data class PropertyBooking(
    val id: String,
    val propertyId: String,
    val propertyName: String,
    val checkIn: String,
    val checkOut: String,
    val nights: Int,
    val totalAmount: Double,
    val currency: String,
    val status: String,
    val createdAt: String
)

@Serializable
data class PropertyFilter(
    val type: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val checkIn: String? = null,
    val checkOut: String? = null
)
