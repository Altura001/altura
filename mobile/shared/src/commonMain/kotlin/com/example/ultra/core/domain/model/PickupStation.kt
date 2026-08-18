package com.example.ultra.core.domain.model

data class PickupStation(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val phone: String? = null,
    val operatingHours: String? = null
)
