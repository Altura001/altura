package com.example.ultra.health.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HealthService(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val price: Double,
    val currency: String = "NGN",
    val durationMinutes: Int = 0,
    val isAvailable: Boolean = true
)

@Serializable
data class HealthProduct(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "NGN",
    val imageUrl: String = "",
    val category: String = "",
    val inStock: Boolean = true
)

@Serializable
data class Appointment(
    val id: String,
    val serviceId: String,
    val serviceName: String,
    val providerName: String,
    val date: String,
    val time: String,
    val status: String,
    val createdAt: String
)
