package com.example.ultra.ticketing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.ticketing.presentation.intent.TicketingAction
import com.example.ultra.ticketing.presentation.intent.TicketingEvent
import com.example.ultra.ticketing.presentation.intent.TicketingState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class TicketingViewModel : ViewModel() {

    private val _state = MutableStateFlow(TicketingState())
    val state = _state.asStateFlow()

    private val _events = Channel<TicketingEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: TicketingAction) {
        // TODO: wire to use cases
    }
}
