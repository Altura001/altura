package com.example.ultra.hotel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.hotel.presentation.intent.HotelAction
import com.example.ultra.hotel.presentation.intent.HotelEvent
import com.example.ultra.hotel.presentation.intent.HotelState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class HotelViewModel : ViewModel() {

    private val _state = MutableStateFlow(HotelState())
    val state = _state.asStateFlow()

    private val _events = Channel<HotelEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: HotelAction) {
        // TODO: wire to use cases
    }
}
