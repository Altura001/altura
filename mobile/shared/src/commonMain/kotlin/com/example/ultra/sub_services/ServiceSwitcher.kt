package com.example.ultra.sub_services

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ultra.core.presentation.theme.AlturaBlue
import com.example.ultra.core.presentation.theme.AlturaCyan
import com.example.ultra.core.presentation.theme.AlturaOrange
import com.example.ultra.core.presentation.theme.AlturaRed
import org.jetbrains.compose.resources.painterResource
import ultra.shared.generated.resources.Res
import ultra.shared.generated.resources.altura_logo
import ultra.shared.generated.resources.bg_service_switcher

@Composable
fun ServiceSwitcherScreen(
    onNavigateBack: () -> Unit,
    onServiceSelected: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_service_switcher),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.1f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar — no back button on launcher (this IS the home)
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "SELECT A SERVICE",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                // Central Logo
                Image(
                    painter = painterResource(Res.drawable.altura_logo),
                    contentDescription = "Altura Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.Center)
                        .offset(y = (-30).dp)
                )

                // Top row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceItem(
                        name = "Food",
                        icon = Icons.Default.Restaurant,
                        color = AlturaOrange,
                        onClick = { onServiceSelected("Food") }
                    )
                    ServiceItem(
                        name = "Rent a Car",
                        icon = Icons.Default.DirectionsCar,
                        color = AlturaRed,
                        onClick = { onServiceSelected("Rent a Car") }
                    )
                }

                // Middle left
                ServiceItem(
                    name = "Ticketing",
                    icon = Icons.AutoMirrored.Filled.AirplaneTicket,
                    color = AlturaCyan,
                    modifier = Modifier.align(Alignment.CenterStart).offset(y = (-30).dp),
                    onClick = { onServiceSelected("Ticketing") }
                )

                // Middle right
                ServiceItem(
                    name = "Shopping",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.align(Alignment.CenterEnd).offset(y = (-30).dp),
                    onClick = { onServiceSelected("Shopping") }
                )

                // Bottom row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 140.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceItem(
                        name = "Hotel/Shortlet",
                        icon = Icons.Default.Hotel,
                        color = Color(0xFF8D6E63),
                        onClick = { onServiceSelected("Hotel") }
                    )
                    ServiceItem(
                        name = "Altura Health",
                        icon = Icons.Default.MedicalServices,
                        color = AlturaBlue,
                        onClick = { onServiceSelected("Health") }
                    )
                }

                // Very Bottom
                ServiceItem(
                    name = "Local Market",
                    icon = Icons.Default.Storefront,
                    color = AlturaBlue,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                    onClick = { onServiceSelected("Local Market") }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ServiceItem(
    name: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            tint = color,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
