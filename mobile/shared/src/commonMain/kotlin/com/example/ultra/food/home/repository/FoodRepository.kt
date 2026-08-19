package com.example.ultra.food.home.repository

import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.food.home.domain.model.FoodOrder
import com.example.ultra.food.home.domain.model.MenuItem
import com.example.ultra.food.home.domain.model.Restaurant

interface FoodRepository {
    suspend fun getRestaurants(): Result<List<Restaurant>, DataError.Network>
    suspend fun getMenu(restaurantId: String): Result<List<MenuItem>, DataError.Network>
    suspend fun placeOrder(restaurantId: String, items: List<Pair<String, Int>>): Result<FoodOrder, DataError.Network>
    suspend fun getOrders(): Result<List<FoodOrder>, DataError.Network>
}
