package com.example.ultra.navigation.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ultra.auth.presentation.auth.screen.AuthScreenRoot
import com.example.ultra.core.presentation.notification.NotificationHost
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.core.routing.AppRoute
import com.example.ultra.core.routing.ShoppingNavItem
import com.example.ultra.food.home.presentation.food.screen.FoodHomeScreenRoot
import com.example.ultra.food.presentation.FoodServiceMainScreen
import com.example.ultra.profile.presentation.profile.screen.ProfileScreenRoot
import com.example.ultra.service_shell.presentation.ServiceSwitcherScreen
import com.example.ultra.shopping.cart.presentation.cart.screen.CartScreenRoot
import com.example.ultra.shopping.category.presentation.category.screen.CategoryScreenRoot
import com.example.ultra.shopping.checkout.presentation.screen.CheckoutScreenRoot
import com.example.ultra.shopping.home.presentation.home.screen.HomeScreenRoot
import com.example.ultra.shopping.home.presentation.productdetail.ProductDetailScreenRoot
import com.example.ultra.shopping.presentation.ShoppingServiceMainScreen
import com.example.ultra.shopping.wishlist.presentation.wishlist.screen.WishlistScreenRoot
import org.koin.compose.koinInject


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val notificationManager: NotificationManager = koinInject()
    val currentNotification by notificationManager.notification.collectAsState()

    // Determine which service is active and whether bottom bar should show
    val currentRoute = currentDestination?.route.orEmpty()
    val isShoppingRoute = currentRoute.contains("Shopping") && !currentRoute.contains("Launcher")
    val isFoodRoute = currentRoute.contains("Food") && !currentRoute.contains("Launcher")

    val showShoppingBottomBar = isShoppingRoute &&
        !currentRoute.contains("ShoppingCheckout") &&
        !currentRoute.contains("ShoppingProductDetail")

    val showFoodBottomBar = isFoodRoute &&
        !currentRoute.contains("FoodRestaurant") &&
        !currentRoute.contains("FoodCheckout")

    val onServiceSelected: (String) -> Unit = { service ->
        when (service) {
            "Shopping" -> navController.navigate(AppRoute.ShoppingHome)
            "Food" -> navController.navigate(AppRoute.FoodHome)
            else -> {
                navController.navigate(AppRoute.Launcher) {
                    popUpTo(AppRoute.Launcher) { inclusive = true }
                }
            }
        }
    }

    Box(Modifier) {
        NavHost(
            navController = navController,
            startDestination = AppRoute.Launcher
        ) {
            // ── Launcher / Service Switcher ──
            composable<AppRoute.Launcher> {
                ServiceSwitcherScreen(
                    onNavigateBack = { },
                    onServiceSelected = onServiceSelected
                )
            }

            // ── Shopping service ──
            composable<AppRoute.ShoppingHome> {
                ShoppingServiceMainScreen(
                    navController = navController,
                    currentDestination = currentDestination,
                    showBottomBar = showShoppingBottomBar,
                    onServiceSelected = onServiceSelected
                ) {
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
            }
            composable<AppRoute.ShoppingCategory> {
                ShoppingServiceMainScreen(
                    navController = navController,
                    currentDestination = currentDestination,
                    showBottomBar = showShoppingBottomBar,
                    onServiceSelected = onServiceSelected
                ) {
                    CategoryScreenRoot()
                }
            }
            composable<AppRoute.ShoppingWishlist> {
                ShoppingServiceMainScreen(
                    navController = navController,
                    currentDestination = currentDestination,
                    showBottomBar = showShoppingBottomBar,
                    onServiceSelected = onServiceSelected
                ) {
                    WishlistScreenRoot(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
            composable<AppRoute.ShoppingCart> {
                ShoppingServiceMainScreen(
                    navController = navController,
                    currentDestination = currentDestination,
                    showBottomBar = showShoppingBottomBar,
                    onServiceSelected = onServiceSelected
                ) {
                    CartScreenRoot(
                        onCheckout = { navController.navigate(AppRoute.ShoppingCheckout) }
                    )
                }
            }
            composable<AppRoute.ShoppingAccount> {
                ShoppingServiceMainScreen(
                    navController = navController,
                    currentDestination = currentDestination,
                    showBottomBar = showShoppingBottomBar,
                    onServiceSelected = onServiceSelected
                ) {
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
            }
            // Checkout and ProductDetail — top bar only, no bottom bar
            composable<AppRoute.ShoppingCheckout> {
                ShoppingServiceMainScreen(
                    navController = navController,
                    currentDestination = currentDestination,
                    showBottomBar = false,
                    onServiceSelected = onServiceSelected
                ) {
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
            composable<AppRoute.ShoppingProductDetail> { backStackEntry ->
                val route: AppRoute.ShoppingProductDetail = backStackEntry.toRoute()
                ShoppingServiceMainScreen(
                    navController = navController,
                    currentDestination = currentDestination,
                    showBottomBar = false,
                    onServiceSelected = onServiceSelected
                ) {
                    ProductDetailScreenRoot(
                        handle = route.handle,
                        navController = navController
                    )
                }
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

            // ── Food service ──
            composable<AppRoute.FoodHome> {
                FoodServiceMainScreen(
                    showBottomBar = showFoodBottomBar,
                    onServiceSelected = onServiceSelected
                ) {
                    FoodHomeScreenRoot(
                        onNavigateToRestaurant = { restaurantId ->
                            // navigate to restaurant detail
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onServiceSelected = onServiceSelected
                    )
                }
            }
        }

        // ── Notification overlay (always on top) ──
        Box(contentAlignment = Alignment.TopCenter) {
            NotificationHost(
                notification = currentNotification,
                onDismiss = { notificationManager.dismiss() }
            )
        }
    }
}
