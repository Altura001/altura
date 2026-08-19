package com.example.ultra.ticketing.domain.usecase

import com.example.ultra.ticketing.data.repository.TicketingRepository

data class TicketingUseCases(
    val getEvents: GetEventsUseCase,
    val purchaseTicket: PurchaseTicketUseCase,
    val getTicketOrders: GetTicketOrdersUseCase
)

class GetEventsUseCase(private val repo: TicketingRepository) {
    suspend operator fun invoke() = repo.getEvents()
}

class PurchaseTicketUseCase(private val repo: TicketingRepository) {
    suspend operator fun invoke(eventId: String, quantity: Int) = repo.purchaseTicket(eventId, quantity)
}

class GetTicketOrdersUseCase(private val repo: TicketingRepository) {
    suspend operator fun invoke() = repo.getOrders()
}
