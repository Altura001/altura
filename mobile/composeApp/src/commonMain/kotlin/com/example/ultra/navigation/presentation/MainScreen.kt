package com.example.ultra.navigation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.ultra.catalog.presentation.catalog.screen.HomeScreenRoot
import com.example.ultra.catalog.presentation.productdetail.ProductDetailScreenRoot
import com.example.ultra.category.presentation.category.screen.CategoryScreenRoot
import com.example.ultra.checkout.presentation.screen.CheckoutScreenRoot
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.profile.presentation.profile.screen.ProfileScreenRoot
import com.example.ultra.wishlist.presentation.category.screen.WishlistScreenRoot
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

enum class BottomNavItem(
    val title: String,
    val route: AppRoute,
    val icon: ImageVector
) {
    HOME( "Home", AppRoute.Home, Icons.Default.Home),
    CATEGORY("Category", AppRoute.Category, Icons.AutoMirrored.Filled.List),
    WISHLIST( "Wishlist", AppRoute.Wishlist, Icons.Default.Favorite),
    CART("Cart", AppRoute.Cart, Icons.Default.ShoppingCart),
    ACCOUNT( "Account", AppRoute.Account, Icons.Default.Person)
}

sealed interface AppRoute {
    val route: String
    @Serializable
    object Auth : AppRoute {
        override val route: String = "auth"
    }

    @Serializable
    object Home : AppRoute {
        override val route: String = "home"
    }
    @Serializable
    object Category : AppRoute {
        override val route: String = "category"
    }
    @Serializable
    object Wishlist : AppRoute {
        override val route: String = "wishlist"
    }
    @Serializable
    object Cart : AppRoute {
        override val route: String = "cart"
    }
    @Serializable
    object Account : AppRoute {
        override val route: String = "account"
    }
    @Serializable
    object Checkout : AppRoute {
        override val route: String = "checkout"
    }

    @Serializable
    data class ProductDetail (val handle: String) : AppRoute {
        override val route: String = "product_detail/$handle"
    }
}


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isOnAuthScreen = currentDestination?.route == AppRoute.Auth.route

    Scaffold(
        bottomBar = {
            if (!isOnAuthScreen) {
                NavigationBar {
                    BottomNavItem.entries.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route.route } == true,
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
            startDestination = AppRoute.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<AppRoute.Home> {
                HomeScreenRoot(
                    onProductClick = { handle ->
                        navController.navigate(AppRoute.ProductDetail(handle = handle))
                    },
                    navController = navController
                )
            }
            composable<AppRoute.Category> {
                CategoryScreenRoot()
            }
            composable<AppRoute.Wishlist> {
                WishlistScreenRoot()
            }
            composable<AppRoute.Cart> {
                val authRepository: AuthRepository = koinInject()
                val isLoggedIn = authRepository.isLoggedIn()

                if (!isLoggedIn) {
                    LoginPromptScreen(
                        onLoginClick = {
                            navController.navigate(AppRoute.Auth) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                } else {
                    CartScreenRoot(
                        onCheckout = { navController.navigate(AppRoute.Checkout) }
                    )
                }
            }
            composable<AppRoute.Account> {
                ProfileScreenRoot(
                    onNavigateToLogin = {
                        navController.navigate(AppRoute.Auth) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<AppRoute.Auth> {
                AuthScreenRoot(
                    onLoginSuccess = {
                        navController.navigate(BottomNavItem.ACCOUNT.route) {
                            popUpTo(AppRoute.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<AppRoute.ProductDetail> { backStackEntry ->
                val route: AppRoute.ProductDetail = backStackEntry.toRoute()

                ProductDetailScreenRoot(
                    handle = route.handle,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<AppRoute.Checkout> {
                CheckoutScreenRoot(
                    onNavigateBack = { navController.popBackStack() },
                    onDone = {
                        navController.navigate(BottomNavItem.HOME.route) {
                            popUpTo(AppRoute.Checkout) { inclusive = true }
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
