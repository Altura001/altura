package com.example.ultra.service_shell.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ultra.services.Service

/**
 * Shared top bar visible inside every service.
 * Background color adapts to [service]'s [Service.accentColor].
 */
@Composable
fun ServiceTopBar(
    service: Service,
    onServiceSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().statusBarsPadding(),
        color = service.accentColor,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopBarItem("Shopping", Icons.Default.ShoppingCart, service == Service.SHOPPING) { onServiceSelected("Shopping") }
            TopBarItem("Food", Icons.Default.Restaurant, service == Service.FOOD) { onServiceSelected("Food") }
            TopBarItem("Local Market", Icons.Default.Storefront, service == Service.LOCAL_MARKET) { onServiceSelected("Local Market") }
            TopBarItem("Rent a Car", Icons.Default.DirectionsCar, service == Service.RENT_A_CAR) { onServiceSelected("Rent a Car") }
            TopBarItem("Hotel", Icons.Default.Hotel, service == Service.HOTEL) { onServiceSelected("Hotel") }
            TopBarItem("Ticketing", Icons.AutoMirrored.Filled.AirplaneTicket, service == Service.TICKETING) { onServiceSelected("Ticketing") }
            TopBarItem("Health", Icons.Default.MedicalServices, service == Service.HEALTH) { onServiceSelected("Health") }
        }
    }
}

@Composable
private fun TopBarItem(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
