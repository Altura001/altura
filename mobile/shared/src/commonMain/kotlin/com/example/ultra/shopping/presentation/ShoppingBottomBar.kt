package com.example.ultra.shopping.presentation

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.example.ultra.core.routing.AppRoute
import com.example.ultra.core.routing.ShoppingNavItem


@Composable
fun ShoppingBottomBar(
    cartItemCount: Int,
    currentDestination: NavDestination?,
    navController: NavHostController,
    showBottomBar: Boolean = true
) {
    if (!showBottomBar) return

    NavigationBar {
        ShoppingNavItem.entries.forEach { item ->
            val badgeCount = if (item == ShoppingNavItem.CART) cartItemCount else 0
            NavigationBarItem(
                icon = {
                    if (badgeCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color(0xFFFF5A00),
                                    contentColor = Color.White
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
