package com.example.ultra.ticketing.data.repository

import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.ticketing.domain.model.Event
import com.example.ultra.ticketing.domain.model.TicketOrder

interface TicketingRepository {
    suspend fun getEvents(): Result<List<Event>, DataError.Network>
    suspend fun purchaseTicket(eventId: String, quantity: Int): Result<TicketOrder, DataError.Network>
    suspend fun getOrders(): Result<List<TicketOrder>, DataError.Network>
}
