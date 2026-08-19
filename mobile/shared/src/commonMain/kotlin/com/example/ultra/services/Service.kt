package com.example.ultra.services

import com.example.ultra.core.presentation.theme.AlturaBlue
import com.example.ultra.core.presentation.theme.AlturaOrange
import androidx.compose.ui.graphics.Color

enum class Service(
    val id: String,
    val displayName: String,
    val routePrefix: String,
    val accentColor: Color
) {
    SHOPPING(
        id = "shopping",
        displayName = "Shopping",
        routePrefix = "shopping",
        accentColor = AlturaOrange
    ),
    FOOD(
        id = "food",
        displayName = "Food",
        routePrefix = "food",
        accentColor = Color(0xFFE53935)
    ),
    RENT_A_CAR(
        id = "rent_a_car",
        displayName = "Rent a Car",
        routePrefix = "rent_a_car",
        accentColor = Color(0xFF1E88E5)
    ),
    TICKETING(
        id = "ticketing",
        displayName = "Ticketing",
        routePrefix = "ticketing",
        accentColor = Color(0xFF8E24AA)
    ),
    HOTEL(
        id = "hotel",
        displayName = "Hotel / Shortlet",
        routePrefix = "hotel",
        accentColor = Color(0xFF00897B)
    ),
    HEALTH(
        id = "health",
        displayName = "Altura Health",
        routePrefix = "health",
        accentColor = Color(0xFF43A047)
    ),
    LOCAL_MARKET(
        id = "local_market",
        displayName = "Local Market",
        routePrefix = "local_market",
        accentColor = AlturaBlue
    )
}
