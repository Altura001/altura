package com.example.ultra.navigation.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ultra.auth.presentation.auth.screen.AuthScreenRoot
import com.example.ultra.shopping.cart.presentation.cart.screen.CartScreenRoot
import com.example.ultra.shopping.category.presentation.category.screen.CategoryScreenRoot
import com.example.ultra.shopping.checkout.presentation.screen.CheckoutScreenRoot
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.presentation.notification.NotificationHost
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.shopping.home.presentation.home.screen.HomeScreenRoot
import com.example.ultra.shopping.home.presentation.productdetail.ProductDetailScreenRoot
import com.example.ultra.profile.presentation.profile.screen.ProfileScreenRoot
import com.example.ultra.sub_services.ServiceSwitcherScreen
import com.example.ultra.shopping.wishlist.presentation.wishlist.screen.WishlistScreenRoot
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

// ── Shopping bottom nav items ──
enum class ShoppingNavItem(
    val title: String,
    val route: AppRoute,
    val icon: ImageVector
) {
    HOME("Home", AppRoute.ShoppingHome, Icons.Default.Home),
    CATEGORY("Category", AppRoute.ShoppingCategory, Icons.AutoMirrored.Filled.List),
    WISHLIST("Wishlist", AppRoute.ShoppingWishlist, Icons.Default.Favorite),
    CART("Cart", AppRoute.ShoppingCart, Icons.Default.ShoppingCart),
    ACCOUNT("Account", AppRoute.ShoppingAccount, Icons.Default.Person)
}

// ── Routes ──
sealed interface AppRoute {
    val route: String

    // Launcher
    @Serializable
    object Launcher : AppRoute {
        override val route: String = "launcher"
    }

    // Shopping service
    @Serializable
    object ShoppingHome : AppRoute {
        override val route: String = "shopping/home"
    }
    @Serializable
    object ShoppingCategory : AppRoute {
        override val route: String = "shopping/category"
    }
    @Serializable
    object ShoppingWishlist : AppRoute {
        override val route: String = "shopping/wishlist"
    }
    @Serializable
    object ShoppingCart : AppRoute {
        override val route: String = "shopping/cart"
    }
    @Serializable
    object ShoppingAccount : AppRoute {
        override val route: String = "shopping/account"
    }
    @Serializable
    object ShoppingCheckout : AppRoute {
        override val route: String = "shopping/checkout"
    }
    @Serializable
    data class ShoppingProductDetail(val handle: String) : AppRoute {
        override val route: String = "shopping/product_detail/$handle"
    }

    // Shared
    @Serializable
    object Auth : AppRoute {
        override val route: String = "auth"
    }
}


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentRoute = currentDestination?.route

    // Show bottom nav only when inside the shopping service
    val isOnShoppingService = currentRoute?.startsWith("shopping/") == true
    val isOnAuthScreen = currentRoute == AppRoute.Auth.route
    val isOnCheckout = currentRoute == AppRoute.ShoppingCheckout.route
    val isOnProductDetail = currentRoute?.startsWith("shopping/product_detail/") == true
    val showBottomBar = isOnShoppingService && !isOnCheckout && !isOnProductDetail && !isOnAuthScreen

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
                    ShoppingNavItem.entries.forEach { item ->
                        val badgeCount = if (item == ShoppingNavItem.CART) cartItemCount else 0
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
                                    popUpTo(AppRoute.ShoppingHome.route) {
                                        saveState = item != ShoppingNavItem.HOME
                                        inclusive = item == ShoppingNavItem.HOME
                                    }
                                    launchSingleTop = true
                                    restoreState = item != ShoppingNavItem.HOME
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
            startDestination = AppRoute.Launcher,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── Launcher / Service Switcher ──
            composable<AppRoute.Launcher> {
                ServiceSwitcherScreen(
                    onNavigateBack = { /* exit app */ },
                    onServiceSelected = { service ->
                        when (service) {
                            "Shopping" -> navController.navigate(AppRoute.ShoppingHome)
                            // Other services will be wired later
                        }
                    }
                )
            }

            // ── Shopping service ──
            composable<AppRoute.ShoppingHome> {
                HomeScreenRoot(
                    onProductClick = { handle ->
                        navController.navigate(AppRoute.ShoppingProductDetail(handle = handle))
                    },
                    onServiceSwitcherClick = {
                        navController.navigate(AppRoute.Launcher) {
                            popUpTo(AppRoute.Launcher) { inclusive = true }
                        }
                    },
                    navController = navController
                )
            }
            composable<AppRoute.ShoppingCategory> {
                CategoryScreenRoot()
            }
            composable<AppRoute.ShoppingWishlist> {
                WishlistScreenRoot(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<AppRoute.ShoppingCart> {
                CartScreenRoot(
                    onCheckout = { navController.navigate(AppRoute.ShoppingCheckout) }
                )
            }
            composable<AppRoute.ShoppingAccount> {
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
                        navController.navigate(ShoppingNavItem.ACCOUNT.route) {
                            popUpTo(AppRoute.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<AppRoute.ShoppingProductDetail> { backStackEntry ->
                val route: AppRoute.ShoppingProductDetail = backStackEntry.toRoute()
                ProductDetailScreenRoot(
                    handle = route.handle,
                    navController = navController
                )
            }
            composable<AppRoute.ShoppingCheckout> {
                CheckoutScreenRoot(
                    onNavigateBack = { navController.popBackStack() },
                    onDone = {
                        navController.navigate(AppRoute.ShoppingHome) {
                            popUpTo(AppRoute.ShoppingCheckout) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
