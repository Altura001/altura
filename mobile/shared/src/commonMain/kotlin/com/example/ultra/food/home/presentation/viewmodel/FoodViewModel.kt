package com.example.ultra.food.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.food.home.presentation.intent.Brand
import com.example.ultra.food.home.presentation.intent.FoodAction
import com.example.ultra.food.home.presentation.intent.FoodCategory
import com.example.ultra.food.home.presentation.intent.FoodEvent
import com.example.ultra.food.home.presentation.intent.FoodState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class FoodViewModel : ViewModel() {

    private val _state = MutableStateFlow(FoodState(
        categories = listOf(
            FoodCategory("Amala", "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?auto=format&fit=crop&w=200&q=80"),
            FoodCategory("Jollof Rice", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=200&q=80"),
            FoodCategory("Suya", "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=200&q=80"),
            FoodCategory("Chicken", "https://images.unsplash.com/photo-1562967914-608f82629710?auto=format&fit=crop&w=200&q=80"),
            FoodCategory("Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=200&q=80")
        ),
        popularBrands = listOf(
            Brand("Item7go", "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&w=100&q=80"),
            Brand("Chicken Republic", "https://images.unsplash.com/photo-1619454016518-697bc23143cd?auto=format&fit=crop&w=100&q=80"),
            Brand("Awoof", "https://images.unsplash.com/photo-1552566626-52f8b828add9?auto=format&fit=crop&w=100&q=80"),
            Brand("Ogo Oluwaniyi", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=100&q=80"),
            Brand("KFC", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=100&q=80")
        )
    ))
    val state = _state.asStateFlow()

    private val _events = Channel<FoodEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: FoodAction) {
        // TODO: wire to use cases
    }
}
