package com.example.ultra.food.presentation.intent

import com.example.ultra.food.domain.model.FoodCart
import com.example.ultra.food.domain.model.FoodOrder
import com.example.ultra.food.domain.model.MenuItem
import com.example.ultra.food.domain.model.Restaurant
import com.example.ultra.core.presentation.UiText

data class FoodState(
    val isLoading: Boolean = false,
    val restaurants: List<Restaurant> = emptyList(),
    val selectedRestaurant: Restaurant? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val cart: FoodCart = FoodCart(),
    val orders: List<FoodOrder> = emptyList(),
    val error: UiText? = null
)

sealed interface FoodAction {
    data object LoadRestaurants : FoodAction
    data class SelectRestaurant(val restaurant: Restaurant) : FoodAction
    data class AddToCart(val item: MenuItem) : FoodAction
    data class RemoveFromCart(val itemId: String) : FoodAction
    data class UpdateQuantity(val itemId: String, val quantity: Int) : FoodAction
    data object PlaceOrder : FoodAction
    data object LoadOrders : FoodAction
    data object ClearError : FoodAction
}

sealed interface FoodEvent {
    data class ShowError(val message: UiText) : FoodEvent
    data object OrderPlaced : FoodEvent
}
