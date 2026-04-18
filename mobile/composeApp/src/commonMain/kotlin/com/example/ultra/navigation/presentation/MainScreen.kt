package com.example.ultra.navigation.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ultra.cart.presentation.cart.screen.CartScreenRoot
import com.example.ultra.catalog.presentation.catalog.screen.CatalogScreenRoot
import com.example.ultra.profile.presentation.profile.screen.ProfileScreenRoot

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Catalog("catalog", "Catalog", Icons.Default.Store),
    Cart("cart", "Cart", Icons.Default.ShoppingCart),
    Profile("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        bottomBar = {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Catalog.route) {
                CatalogScreenRoot()
            }
            composable(BottomNavItem.Cart.route) {
                CartScreenRoot()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreenRoot()
            }
        }
    }
}
