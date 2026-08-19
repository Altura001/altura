package com.example.ultra.rent_a_car.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val id: String,
    val name: String,
    val brand: String,
    val type: String,
    val imageUrl: String,
    val pricePerDay: Double,
    val currency: String = "NGN",
    val isAvailable: Boolean = true,
    val seats: Int = 5,
    val fuelType: String = "Petrol",
    val transmission: String = "Automatic",
    val location: String = ""
)

@Serializable
data class VehicleBooking(
    val id: String,
    val vehicleId: String,
    val vehicleName: String,
    val startDate: String,
    val endDate: String,
    val totalDays: Int,
    val totalAmount: Double,
    val currency: String,
    val status: String,
    val createdAt: String
)

@Serializable
data class VehicleFilter(
    val type: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val seats: Int? = null,
    val transmission: String? = null
)
