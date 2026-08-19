package com.example.ultra.food.presentation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FoodBottomNavigation(
    showBottomBar: Boolean = true
) {
    if (!showBottomBar) return

    NavigationBar(
        containerColor = Color(0xFF2E7D32),
        contentColor = Color.White,
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(32.dp))
            .height(56.dp),
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
            label = { Text("Home", color = Color.White, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFFFBC02D).copy(alpha = 0.8f),
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White.copy(alpha = 0.7f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Search, contentDescription = "Explore", tint = Color.White) },
            label = { Text("Explore", color = Color.White, fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White) },
            label = { Text("Cart", color = Color.White, fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Person, contentDescription = "Account", tint = Color.White) },
            label = { Text("Account", color = Color.White, fontSize = 10.sp) }
        )
    }
}
