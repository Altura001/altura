package com.example.ultra.ticketing.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val venue: String,
    val date: String,
    val time: String,
    val price: Double,
    val currency: String = "NGN",
    val ticketsAvailable: Int = 0,
    val isSoldOut: Boolean = false
)

@Serializable
data class Ticket(
    val id: String,
    val eventId: String,
    val eventName: String,
    val ticketType: String,
    val price: Double,
    val currency: String,
    val qrCode: String = "",
    val status: String
)

@Serializable
data class TicketOrder(
    val id: String,
    val eventId: String,
    val eventName: String,
    val tickets: List<Ticket>,
    val totalAmount: Double,
    val currency: String,
    val status: String,
    val createdAt: String
)
