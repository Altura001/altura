package com.example.ultra.rent_a_car.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.rent_a_car.presentation.intent.RentACarAction
import com.example.ultra.rent_a_car.presentation.intent.RentACarEvent
import com.example.ultra.rent_a_car.presentation.intent.RentACarState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class RentACarViewModel : ViewModel() {

    private val _state = MutableStateFlow(RentACarState())
    val state = _state.asStateFlow()

    private val _events = Channel<RentACarEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: RentACarAction) {
        // TODO: wire to use cases
    }
}
