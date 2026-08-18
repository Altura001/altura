package com.example.ultra.navigation.presentation

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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ultra.auth.presentation.auth.screen.AuthScreenRoot
import com.example.ultra.cart.presentation.cart.screen.CartScreenRoot
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.presentation.notification.NotificationHost
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.home.presentation.home.screen.HomeScreenRoot
import com.example.ultra.home.presentation.productdetail.ProductDetailScreenRoot
import com.example.ultra.category.presentation.category.screen.CategoryScreenRoot
import com.example.ultra.checkout.presentation.screen.CheckoutScreenRoot
import com.example.ultra.profile.presentation.profile.screen.ProfileScreenRoot
import com.example.ultra.wishlist.presentation.wishlist.screen.WishlistScreenRoot
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box

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
    val isOnCheckout = currentDestination?.route == AppRoute.Checkout.route
    val isOnProductDetail = currentDestination?.route?.startsWith("product_detail/") == true
    val showBottomBar = !isOnAuthScreen && !isOnCheckout && !isOnProductDetail

    val cartRepository: CartRepository = koinInject()
    val cart by cartRepository.observeCart().collectAsState(initial = com.example.ultra.core.domain.model.Cart())
    val cartItemCount = cart.itemCount

    val notificationManager: NotificationManager = koinInject()
    val currentNotification by notificationManager.notification.collectAsState()

    Scaffold(
        snackbarHost = {
            Box(contentAlignment = Alignment.TopCenter) {
                NotificationHost(
                    notification = currentNotification,
                    onDismiss = { notificationManager.dismiss() }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.entries.forEach { item ->
                        val badgeCount = if (item == BottomNavItem.CART) cartItemCount else 0
                        NavigationBarItem(
                            icon = {
                                if (badgeCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = androidx.compose.ui.graphics.Color(0xFFFF5A00),
                                                contentColor = androidx.compose.ui.graphics.Color.White
                                            ) {
                                                Text(badgeCount.toString(), fontSize = 10.sp)
                                            }
                                        }
                                    ) {
                                        Icon(item.icon, contentDescription = item.title)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = item != BottomNavItem.HOME
                                        inclusive = item == BottomNavItem.HOME
                                    }
                                    launchSingleTop = true
                                    restoreState = item != BottomNavItem.HOME
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
                WishlistScreenRoot(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<AppRoute.Cart> {
                CartScreenRoot(
                    onCheckout = { navController.navigate(AppRoute.Checkout) }
                )
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
                    navController = navController
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
