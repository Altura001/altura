package com.example.ultra.food.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ultra.service_shell.presentation.ServiceTopBar
import com.example.ultra.services.Service

/**
 * Wraps every screen in the Food service.
 * - ServiceTopBar is always visible
 * - FoodBottomNavigation is conditional (hidden on restaurant detail, checkout, etc.)
 */
@Composable
fun FoodServiceMainScreen(
    showBottomBar: Boolean,
    onServiceSelected: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            ServiceTopBar(service = Service.FOOD, onServiceSelected = onServiceSelected)
        },
        bottomBar = {
            FoodBottomNavigation(showBottomBar = showBottomBar)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
