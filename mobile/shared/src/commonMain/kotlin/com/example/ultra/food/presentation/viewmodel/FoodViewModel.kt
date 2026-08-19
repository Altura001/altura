package com.example.ultra.food.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.food.presentation.intent.FoodAction
import com.example.ultra.food.presentation.intent.FoodEvent
import com.example.ultra.food.presentation.intent.FoodState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class FoodViewModel : ViewModel() {

    private val _state = MutableStateFlow(FoodState())
    val state = _state.asStateFlow()

    private val _events = Channel<FoodEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: FoodAction) {
        // TODO: wire to use cases
    }
}
