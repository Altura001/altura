package com.example.ultra.shopping.cart.presentation.cart.screen

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
import com.example.ultra.shopping.cart.presentation.intent.CartAction
import com.example.ultra.shopping.cart.presentation.intent.CartState
import com.example.ultra.shopping.cart.presentation.viewmodel.CartViewModel
import com.example.ultra.core.domain.model.CartItem
import com.example.ultra.core.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel
import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product

@Composable
fun CartScreenRoot(
    viewModel: CartViewModel = koinViewModel(),
    onCheckout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CartScreen(
        state = state,
        onAction = viewModel::onAction,
        onCheckout = onCheckout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartState,
    onAction: (CartAction) -> Unit,
    onCheckout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cart = state.cart
    val itemCount = cart?.itemCount ?: 0
    val cartTotal = cart?.total ?: 0.0

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
//                navigationIcon = {
//                    IconButton(onClick = { /* Handle back */ }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            modifier = Modifier.size(28.dp)
//                        )
//                    }
//                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CheckoutFooter(
                    totalAmount = cartTotal,
                    onCheckout = onCheckout
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (cart == null || cart.isEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Browse products and add items to your cart",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                item {
                    SubtotalSection(amount = cart.subtotal)
                }

                item {
                    Text(
                        text = "Cart ($itemCount)",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }

                items(cart.items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onRemove = { onAction(CartAction.RemoveItem(item.id)) },
                        onIncrement = { onAction(CartAction.UpdateQuantity(item.id, item.quantity + 1)) },
                        onDecrement = {
                            if (item.quantity > 1) {
                                onAction(CartAction.UpdateQuantity(item.id, item.quantity - 1))
                            } else {
                                onAction(CartAction.RemoveItem(item.id))
                            }
                        }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)
                }

                item {
                    CustomersAlsoViewedSection()
                }
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
    item: CartItem,
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
                // Product image placeholder
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title.ifBlank { item.product?.name ?: "Product" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.variantId.isNotBlank()) {
                    Text(
                        text = "Variation: ${item.variantId.take(20)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "N ${formatPrice(item.unitPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (item.product?.oldPrice != null && item.product.oldPrice > item.unitPrice) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "N ${formatPrice(item.product.oldPrice)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val discount = ((1 - item.unitPrice / item.product.oldPrice) * 100).toInt()
                        if (discount > 0) {
                            Surface(
                                color = Color(0xFFFEF9E7),
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Text(
                                    text = "-$discount%",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFF1C40F),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
                Text(
                    text = if (item.product?.inStock != false) "In Stock" else "Out of Stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.product?.inStock != false) Color.Black else Color.Red,
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
                    text = item.quantity.toString(),
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
            shape = RoundedCornerShape(4.dp),
            enabled = totalAmount > 0
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

@Composable
fun CartScreenPreview() {
    UltraTheme {
        CartScreen(
            state = CartState(),
            onAction = {}
        )
    }
}
