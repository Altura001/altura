package com.example.ultra.core.routing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

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