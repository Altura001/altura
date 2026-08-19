package com.example.ultra.shopping.wishlist.presentation.wishlist.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.core.data.util.formatTwoDecimals
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.presentation.theme.*
import com.example.ultra.searchbar.presentation.screen.SearchBar
import com.example.ultra.shopping.wishlist.presentation.intent.WishlistAction
import com.example.ultra.shopping.wishlist.presentation.intent.WishlistState
import com.example.ultra.shopping.wishlist.viewmodel.WishlistViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WishlistScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: WishlistViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    WishlistScreen(
        state = state,
        onAction = { action ->
            when (action) {
                WishlistAction.GoBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    state: WishlistState,
    onAction: (WishlistAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onAction(WishlistAction.GoBack) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                title = {
                    Text(
                        text = "My Wishlist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.items.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your wishlist is empty",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.items, key = { it.id }) { product ->
                        WishlistItem(
                            product = product,
                            onRemove = { onAction(WishlistAction.RemoveFromWishlist(product.id)) },
                            onAddToCart = { onAction(WishlistAction.AddToCart(product)) }
                        )
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistItem(
    product: Product,
    onRemove: () -> Unit,
    onAddToCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        KamelImage(
            resource = { asyncPainterResource(data = product.thumbnailUrl ?: product.imageUrl ?: "") },
            contentDescription = product.name,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF9F9F9)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2
            )

            if (product.category != null) {
                Text(
                    text = product.category.uppercase(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Text(
                text = "N ${product.price.formatTwoDecimals()}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (product.oldPrice != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "N ${product.oldPrice.formatTwoDecimals()}",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val discount = ((product.oldPrice - product.price) / product.oldPrice * 100).toInt()
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF9C4), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-$discount%",
                            fontSize = 10.sp,
                            color = Color(0xFFFBC02D),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remove",
                    fontSize = 14.sp,
                    color = AlturaGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRemove() }
                )

                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = AlturaCyan),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Add To Cart", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
