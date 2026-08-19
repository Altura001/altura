package com.example.ultra.food.home.presentation.intent

import com.example.ultra.core.presentation.UiText
import com.example.ultra.food.home.domain.model.FoodCart
import com.example.ultra.food.home.domain.model.FoodOrder
import com.example.ultra.food.home.domain.model.MenuItem
import com.example.ultra.food.home.domain.model.Restaurant

data class FoodState(
    val isLoading: Boolean = false,
    val categories: List<FoodCategory> = emptyList(),
    val popularBrands: List<Brand> = emptyList(),
    val restaurants: List<Restaurant> = emptyList(),
    val selectedRestaurant: Restaurant? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val cart: FoodCart = FoodCart(),
    val orders: List<FoodOrder> = emptyList(),
    val error: UiText? = null
)

data class FoodCategory(
    val name: String,
    val imageUrl: String
)

data class Brand(
    val name: String,
    val imageUrl: String
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
