package com.example.ultra.food.home.domain.usecase

import com.example.ultra.food.home.repository.FoodRepository


data class FoodUseCases(
    val getRestaurants: GetRestaurantsUseCase,
    val getMenu: GetMenuUseCase,
    val placeFoodOrder: PlaceFoodOrderUseClass,
    val getFoodOrders: GetFoodOrdersUseCase
)

class GetRestaurantsUseCase(private val repo: FoodRepository) {
    suspend operator fun invoke() = repo.getRestaurants()
}

class GetMenuUseCase(private val repo: FoodRepository) {
    suspend operator fun invoke(restaurantId: String) = repo.getMenu(restaurantId)
}

class PlaceFoodOrderUseClass(private val repo: FoodRepository) {
    suspend operator fun invoke(restaurantId: String, items: List<Pair<String, Int>>) =
        repo.placeOrder(restaurantId, items)
}

class GetFoodOrdersUseCase(private val repo: FoodRepository) {
    suspend operator fun invoke() = repo.getOrders()
}
