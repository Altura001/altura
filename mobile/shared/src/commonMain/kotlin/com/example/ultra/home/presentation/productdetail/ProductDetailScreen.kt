package com.example.ultra.home.presentation.productdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavHostController
import com.example.ultra.core.data.util.formatTwoDecimals
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.presentation.ObserveAsEvents
import com.example.ultra.core.presentation.theme.*
import com.example.ultra.navigation.presentation.AppRoute
import com.example.ultra.searchbar.presentation.screen.SearchBar
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDetailScreenRoot(
    handle: String,
    viewModel: ProductDetailViewModel = koinViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val cartRepository: CartRepository = koinInject()
    val cart by cartRepository.observeCart().collectAsState(initial = com.example.ultra.core.domain.model.Cart())
    val cartItemCount = cart.itemCount

    LaunchedEffect(handle) {
        viewModel.onAction(ProductDetailAction.LoadProduct(handle))
    }

    ProductDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onRetry = { viewModel.onAction(ProductDetailAction.LoadProduct(handle)) },
        onNavigateBack = { navController.popBackStack() },
        cartItemCount = cartItemCount,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    state: ProductDetailState,
    onAction: (ProductDetailAction) -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    cartItemCount: Int = 0,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AlturaTextPrimary
                        )
                    }
                },
                title = {
                    SearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(horizontal = 4.dp),
                        placeholder = "Search for Altura"
                    )
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = AlturaActionOrange,
                                    contentColor = Color.White
                                ) {
                                    Text(cartItemCount.toString())
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(onClick = {navController.navigate(AppRoute.Cart) }) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = AlturaTextPrimary
                            )
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = AlturaTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            if (state.product != null) {
                ProductActionBar(
                    onAddToCart = { onAction(ProductDetailAction.AddToCart(state.product!!)) },
                    onIncrement = { onAction(ProductDetailAction.IncrementQuantity(state.product!!)) },
                    onDecrement = { onAction(ProductDetailAction.DecrementQuantity(state.product!!)) },
                    cartQuantity = state.cartQuantity
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    ErrorState(message = state.error.asString(), onRetry = onRetry)
                }

                state.product == null -> {
                    Text(
                        text = "Product not found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    ProductDetailContent(
                        product = state.product,
                        isWishlisted = state.isWishlisted,
                        onToggleWishlist = { onAction(ProductDetailAction.ToggleWishlist(state.product)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: com.example.ultra.core.domain.model.Product,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Product Image Section
        ProductImageSection(
            product = product,
            isWishlisted = isWishlisted,
            onToggleWishlist = onToggleWishlist
        )

        // Vendor Section
        VendorSection(vendorName = "Merchant Store")

        // Product Info Section
        ProductInfoSection(product)

        // Sections
        SectionHeader("Product Description")
        ProductDescription(product.description)

        SectionHeader("Delivery and return info")
        DeliveryAndReturnSection()
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProductImageSection(
    product: com.example.ultra.core.domain.model.Product,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 6 })
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .background(Color.White)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            KamelImage(
                resource = { asyncPainterResource(data = product.imageUrl ?: "") },
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Overlay Icons
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconContainer(Icons.Outlined.Share)
            IconContainer(
                icon = if (isWishlisted) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                tint = if (isWishlisted) AlturaRed else Color.Black,
                onClick = onToggleWishlist
            )
        }

        // Pager Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(6) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (index == pagerState.currentPage) AlturaRed else AlturaDotInactive)
                )
            }
        }
    }
}

@Composable
fun IconContainer(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.8f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = tint)
    }
}

@Composable
fun VendorSection(vendorName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AlturaAvatarBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = vendorName.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sold by",
                    fontSize = 12.sp,
                    color = AlturaTextSecondaryGray
                )
                Text(
                    text = " | ",
                    fontSize = 12.sp,
                    color = AlturaTextSecondaryGray
                )
                Text(
                    text = vendorName,
                    fontSize = 12.sp,
                    color = AlturaRed,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = vendorName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AlturaTextPrimary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AlturaTextSecondaryGray
        )
    }
}

@Composable
fun ProductInfoSection(product: com.example.ultra.core.domain.model.Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = product.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AlturaTextPrimary,
            lineHeight = 26.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "N${product.price.formatTwoDecimals().replace(".00", "").reversed().chunked(3).joinToString(",").reversed()}",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AlturaTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Product Code: ",
                fontSize = 14.sp,
                color = AlturaTextSecondaryGray
            )
            Text(
                text = product.id.take(8).uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AlturaTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (product.inStock) "In Stock" else "Out of Stock",
            fontSize = 14.sp,
            color = if (product.inStock) Color(0xFF4CAF50) else AlturaRed,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlturaHeaderBlue)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProductDescription(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val bullets = listOf(
            "Battery 5100mAh, 11.1v, Solar Panel 20W, 16V Polycrystalline PET laminated solar panel with 6-meter wire",
            "Solar Charge Indicator Digital LED meter displays the charging input power level on a scale of 1 to 4"
        )
        
        if (description.isNotBlank() && description.length > 100) {
            description.split("\n").filter { it.isNotBlank() }.forEach { line ->
                BulletPoint(line)
            }
        } else {
            bullets.forEach { BulletPoint(it) }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "• ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(
            text = text,
            fontSize = 14.sp,
            color = AlturaTextPrimary,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun DeliveryAndReturnSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Choose Location",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LocationDropdown("Lagos")
        Spacer(modifier = Modifier.height(8.dp))
        LocationDropdown("LEKKI-AJAH (SANGOTEDO)")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ServiceItem(
            icon = Icons.Outlined.CardGiftcard,
            title = "Pickup Station",
            description = "Delivery Fees ₦1, 500\nReady for pickup between 08 May and 11 may, if you place order within the next 4hurs 40Mins",
            linkText = "Detail"
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AlturaLightGray)
        
        ServiceItem(
            icon = Icons.Default.LocalShipping,
            title = "Door Delivery",
            description = "Delivery Fees ₦2, 500\nReady for pickup between 08 May and 11 may, if you place order within the next 4hurs 40Mins",
            linkText = "Detail"
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AlturaLightGray)
        
        ServiceItem(
            icon = Icons.Outlined.Inventory,
            title = "Return Policy",
            description = "Delivery Fees ₦2, 500\nReady for pickup between 08 May and 11 may, if you place order within the next 4hurs 40Mins",
            linkText = "Detail"
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AlturaLightGray)
        
        ServiceItem(
            icon = Icons.Outlined.Shield,
            title = "Warranty",
            description = "Standard 2 years warranty",
            linkText = null
        )
    }
}

@Composable
fun LocationDropdown(selected: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AlturaLightGray, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = selected, fontSize = 14.sp, color = AlturaTextPrimary)
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = AlturaTextSecondaryGray)
    }
}

@Composable
fun ServiceItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    linkText: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, AlturaLightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = AlturaTextPrimary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (linkText != null) {
                    Text(
                        text = linkText,
                        fontSize = 12.sp,
                        color = AlturaHeaderBlue,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = AlturaTextSecondaryGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ProductActionBar(
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    cartQuantity: Int
) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedIconButton(
                onClick = { },
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AlturaLightGray)
            ) {
                Icon(Icons.Outlined.Home, contentDescription = null, tint = AlturaTextPrimary)
            }
            OutlinedIconButton(
                onClick = { },
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AlturaLightGray)
            ) {
                Icon(Icons.Outlined.Phone, contentDescription = null, tint = AlturaTextPrimary)
            }

            if (cartQuantity > 0) {
                // Quantity selector
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Button(
                        onClick = onDecrement,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlturaActionOrange,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cartQuantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AlturaTextPrimary
                        )
                    }
                    Button(
                        onClick = onIncrement,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlturaActionOrange,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            } else {
                // Add to cart button
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlturaActionOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "ADD TO CART",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
