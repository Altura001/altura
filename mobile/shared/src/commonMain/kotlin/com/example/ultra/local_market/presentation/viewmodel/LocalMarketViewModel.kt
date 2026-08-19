package com.example.ultra.local_market.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.local_market.presentation.intent.LocalMarketAction
import com.example.ultra.local_market.presentation.intent.LocalMarketEvent
import com.example.ultra.local_market.presentation.intent.LocalMarketState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class LocalMarketViewModel : ViewModel() {

    private val _state = MutableStateFlow(LocalMarketState())
    val state = _state.asStateFlow()

    private val _events = Channel<LocalMarketEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: LocalMarketAction) {
        // TODO: wire to use cases
    }
}
