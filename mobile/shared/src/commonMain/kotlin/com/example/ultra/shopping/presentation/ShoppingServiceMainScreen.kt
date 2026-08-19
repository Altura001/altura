package com.example.ultra.shopping.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.service_shell.presentation.ServiceTopBar
import com.example.ultra.services.Service
import org.koin.compose.koinInject

/**
 * Wraps every screen in the Shopping service.
 * - ServiceTopBar is always visible
 * - ShoppingBottomBar is conditional (hidden on Checkout, ProductDetail, etc.)
 */
@Composable
fun ShoppingServiceMainScreen(
    navController: NavHostController,
    currentDestination: NavDestination?,
    showBottomBar: Boolean,
    onServiceSelected: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val cartRepository: CartRepository = koinInject()
    val cart by cartRepository.observeCart().collectAsState(initial = com.example.ultra.core.domain.model.Cart())

    Column(modifier = Modifier.fillMaxSize()) {
        ServiceTopBar(service = Service.SHOPPING, onServiceSelected = onServiceSelected)

        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            content()
        }

        if (showBottomBar) {
            ShoppingBottomBar(
                cartItemCount = cart.itemCount,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}
