package com.example.ultra.navigation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ultra.auth.presentation.auth.screen.AuthScreenRoot
import com.example.ultra.cart.presentation.cart.screen.CartScreenRoot
import com.example.ultra.catalog.presentation.catalog.screen.CatalogScreenRoot
import com.example.ultra.catalog.presentation.productdetail.ProductDetailScreenRoot
import com.example.ultra.checkout.presentation.screen.CheckoutScreenRoot
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.profile.presentation.profile.screen.ProfileScreenRoot
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

private const val ROUTE_AUTH = "auth"
//private const val ROUTE_PRODUCT_DETAIL = "product_detail/{handle}"
private const val ROUTE_CHECKOUT = "checkout"

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Catalog("catalog", "Catalog", Icons.Default.Store),
    Cart("cart", "Cart", Icons.Default.ShoppingCart),
    Profile("profile", "Profile", Icons.Default.Person)
}


@Serializable
data class ProductDetail(val handle: String)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isOnAuthScreen = currentDestination?.route == ROUTE_AUTH

    Scaffold(
        bottomBar = {
            if (!isOnAuthScreen) {
                NavigationBar {
                    BottomNavItem.entries.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Catalog.route) {
                CatalogScreenRoot(
                    onProductClick = { handle ->
                        navController.navigate(ProductDetail(handle = "your-product-handle"))
                    }
                )
            }
            composable(BottomNavItem.Cart.route) {
                val authRepository: AuthRepository = koinInject()
                val isLoggedIn = authRepository.isLoggedIn()

                if (!isLoggedIn) {
                    LoginPromptScreen(
                        onLoginClick = {
                            navController.navigate(ROUTE_AUTH) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                } else {
                    CartScreenRoot(
                        onCheckout = { navController.navigate(ROUTE_CHECKOUT) }
                    )
                }
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreenRoot(
                    onNavigateToLogin = {
                        navController.navigate(ROUTE_AUTH) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(ROUTE_AUTH) {
                AuthScreenRoot(
                    onLoginSuccess = {
                        navController.navigate(BottomNavItem.Profile.route) {
                            popUpTo(ROUTE_AUTH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<ProductDetail> { backStackEntry ->
                val route: ProductDetail = backStackEntry.toRoute()

                ProductDetailScreenRoot(
                    handle = route.handle,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(ROUTE_CHECKOUT) {
                CheckoutScreenRoot(
                    onNavigateBack = { navController.popBackStack() },
                    onDone = {
                        navController.navigate(BottomNavItem.Catalog.route) {
                            popUpTo(ROUTE_CHECKOUT) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginPromptScreen(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sign in to view your cart", style = MaterialTheme.typography.titleMedium)
        Text(
            "Please sign in to add items to your cart and check out",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginClick) { Text("Sign In") }
    }
}
