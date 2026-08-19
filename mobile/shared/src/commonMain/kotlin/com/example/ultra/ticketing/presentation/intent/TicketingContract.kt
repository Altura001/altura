package com.example.ultra.ticketing.presentation.intent

import com.example.ultra.core.presentation.UiText
import com.example.ultra.ticketing.domain.model.Event
import com.example.ultra.ticketing.domain.model.TicketOrder

data class TicketingState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val selectedEvent: Event? = null,
    val ticketQuantity: Int = 1,
    val orders: List<TicketOrder> = emptyList(),
    val error: UiText? = null
)

sealed interface TicketingAction {
    data object LoadEvents : TicketingAction
    data class SelectEvent(val event: Event) : TicketingAction
    data class SetQuantity(val quantity: Int) : TicketingAction
    data object PurchaseTicket : TicketingAction
    data object LoadOrders : TicketingAction
    data object ClearError : TicketingAction
}

sealed interface TicketingEvent {
    data class ShowError(val message: UiText) : TicketingEvent
    data object TicketPurchased : TicketingEvent
}
