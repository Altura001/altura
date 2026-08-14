package com.example.ultra.cart.presentation.cart.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.cart.presentation.intent.CartAction
import com.example.ultra.cart.presentation.intent.CartEvent
import com.example.ultra.cart.presentation.intent.CartState
import com.example.ultra.cart.presentation.viewmodel.CartViewModel
import com.example.ultra.core.presentation.ObserveAsEvents
import com.example.ultra.core.presentation.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.ultra.core.domain.model.CartItem
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Cart

@Composable
fun CartScreenRoot(
    viewModel: CartViewModel = koinViewModel(),
    onCheckout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CartEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(event.message.asString())
            }
        }
    }

    CartScreen(
        state = state,
        onAction = viewModel::onAction,
        onCheckout = onCheckout,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartState,
    onAction: (CartAction) -> Unit,
    onCheckout: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CheckoutFooter(
                    totalAmount = 280129.0,
                    onCheckout = onCheckout
                )
                BottomNavigationBar()
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            item {
                SubtotalSection(amount = 290172.0)
            }

            item {
                Text(
                    text = "Cart (3)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
            }

            items(3) { index ->
                CartItemRow(
                    quantity = if (index == 0) 2 else 5,
                    onRemove = { /* Handle remove */ },
                    onIncrement = { /* Handle increment */ },
                    onDecrement = { /* Handle decrement */ }
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)
            }

            item {
                CustomersAlsoViewedSection()
            }
        }
    }
}

@Composable
private fun SubtotalSection(amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9F9))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Subtotal",
            color = Color.Black,
            fontSize = 16.sp
        )
        Text(
            text = "N ${formatPrice(amount)}",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CartItemRow(
    quantity: Int,
    onRemove: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFF2F3F4), RoundedCornerShape(4.dp))
            ) {
                // Placeholder for product image
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "COOKWARE POT COMBINATION COVER (DLB2114)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Variation ... POT SET",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = "N 191, 250",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "N 250, 000",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFFEF9E7),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(
                            text = "-24%",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFF1C40F),
                            fontSize = 10.sp
                        )
                    }
                }
                Text(
                    text = "Few units left",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontSize = 11.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Remove",
                color = AlturaYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.clickable { onRemove() }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp, 30.dp)
                        .background(Color(0xFFF5B7B1), RoundedCornerShape(4.dp))
                        .clickable { onDecrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                )

                Box(
                    modifier = Modifier
                        .size(54.dp, 36.dp)
                        .background(AlturaYellow, RoundedCornerShape(4.dp))
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.Black, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun CheckoutFooter(
    totalAmount: Double,
    onCheckout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .border(1.dp, Color(0xFF2DC8B4), RoundedCornerShape(4.dp))
                .clickable { /* Handle call */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = Color(0xFF2DC8B4)
            )
        }

        Button(
            onClick = onCheckout,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AlturaCyan),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = "Checkout (N ${formatPrice(totalAmount)})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CustomersAlsoViewedSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDF2F2))
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Customers also viewed",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) {
                ProductSmallCard()
            }
        }
    }
}

@Composable
private fun ProductSmallCard() {
    Card(
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFF2F3F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Cooking Pot Stand",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "N 28, 000",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "N 30, 000",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        BottomNavItem("Home", Icons.Default.Home, false)
        BottomNavItem("Category", Icons.AutoMirrored.Filled.List, false)
        BottomNavItem("Wishlist", Icons.Default.FavoriteBorder, false)
        BottomNavItem("Cart", Icons.Default.ShoppingCart, true)
        BottomNavItem("Account", Icons.Default.PersonOutline, false)
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFFFE5A00) else Color.Gray
            )
        },
        label = {
            Text(
                text = label,
                color = if (selected) Color(0xFFFE5A00) else Color.Gray,
                fontSize = 10.sp
            )
        },
        selected = selected,
        onClick = { },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}

private fun formatPrice(price: Double): String {
    val s = price.toInt().toString()
    return if (s.length > 3) {
        val firstPart = s.substring(0, s.length - 3)
        val secondPart = s.substring(s.length - 3)
        "$firstPart, $secondPart"
    } else {
        s
    }
}

@Preview
@Composable
fun CartScreenPreview() {
    UltraTheme {
        CartScreen(
            state = CartState(),
            onAction = {}
        )
    }
}

