package com.example.ultra.health.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.health.presentation.intent.HealthAction
import com.example.ultra.health.presentation.intent.HealthEvent
import com.example.ultra.health.presentation.intent.HealthState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class HealthViewModel : ViewModel() {

    private val _state = MutableStateFlow(HealthState())
    val state = _state.asStateFlow()

    private val _events = Channel<HealthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: HealthAction) {
        // TODO: wire to use cases
    }
}
