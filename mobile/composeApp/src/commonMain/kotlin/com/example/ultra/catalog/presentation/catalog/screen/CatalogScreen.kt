package com.example.ultra.catalog.presentation.catalog.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.catalog.presentation.intent.CatalogAction
import com.example.ultra.catalog.presentation.intent.CatalogState
import com.example.ultra.catalog.presentation.viewmodel.CatalogViewModel
import com.example.ultra.core.data.util.formatTwoDecimals
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.presentation.theme.AlturaBackground
import com.example.ultra.core.presentation.theme.AlturaBlue
import com.example.ultra.core.presentation.theme.AlturaCyan
import com.example.ultra.core.presentation.theme.AlturaDotInactive
import com.example.ultra.core.presentation.theme.AlturaGold
import com.example.ultra.core.presentation.theme.AlturaOrange
import com.example.ultra.core.presentation.theme.AlturaRed
import com.example.ultra.core.presentation.theme.AlturaSearchBg
import com.example.ultra.core.presentation.theme.AlturaTeal
import com.example.ultra.core.presentation.theme.AlturaTextSecondary
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel

private val sectionHeaderColors = listOf(
	AlturaOrange, AlturaTeal, AlturaRed, AlturaBlue
)

@Composable
fun CatalogScreenRoot(
	viewModel: CatalogViewModel = koinViewModel(),
	onProductClick: (String) -> Unit = {},
	onAddToCart: (Product) -> Unit = {}
) {
	val state by viewModel.state.collectAsStateWithLifecycle()

	CatalogScreen(
		state = state,
		onAction = viewModel::onAction,
		onProductClick = onProductClick,
		onAddToCart = { product -> viewModel.onAction(CatalogAction.AddToCart(product)) }
	)
}

@Composable
fun CatalogScreen(
	state: CatalogState,
	onAction: (CatalogAction) -> Unit,
	onProductClick: (String) -> Unit = {},
	onAddToCart: (Product) -> Unit = {},
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.background(AlturaBackground)
	) {
		// Top search bar (always visible)
		CatalogTopBar(
			showBack = state.selectedVendor != null,
			title = state.selectedVendor?.name,
			onBack = { onAction(CatalogAction.ClearSelection) }
		)

		when {
			state.isLoading -> {
				Box(modifier = Modifier.fillMaxSize()) {
					Text("IsLoading")
					CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
				}
			}

			state.error != null -> {
				Box(modifier = Modifier.fillMaxSize()) {
					Column(
						modifier = Modifier
							.align(Alignment.Center)
							.padding(24.dp),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(12.dp)
					) {
						Text(
							text = state.error.asString(),
							color = MaterialTheme.colorScheme.error,
							style = MaterialTheme.typography.bodyLarge
						)
						Button(onClick = {
							onAction(CatalogAction.ClearError)
							onAction(CatalogAction.LoadVendors)
							onAction(CatalogAction.LoadAllProducts)
						}) {
							Text("Retry")
						}
					}
				}
			}

			state.selectedVendor != null -> {
				// Vendor drill-down: product grid for selected vendor
				VendorProductsContent(
					vendor = state.selectedVendor,
					products = state.products,
					onProductClick = onProductClick,
					onAddToCart = onAddToCart
				)
			}

			else -> {
				// Home catalog view
				HomeCatalogContent(
					vendors = state.vendors,
					products = state.products,
					onVendorClick = { onAction(CatalogAction.SelectVendor(it)) },
					onProductClick = onProductClick,
					onAddToCart = onAddToCart
				)
			}
		}
	}
}

// ---------------------------------------------------------------------------
// Top bar
// ---------------------------------------------------------------------------

@Composable
private fun CatalogTopBar(
	showBack: Boolean,
	title: String?,
	onBack: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(Color.White)
			.padding(horizontal = 4.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp)
	) {
		if (showBack) {
			IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
				Icon(
					Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Back",
					tint = Color.Black
				)
			}
			Text(
				text = title ?: "Catalog",
				fontWeight = FontWeight.Bold,
				fontSize = 16.sp,
				modifier = Modifier.weight(1f).padding(start = 4.dp)
			)
		} else {
			// Search bar
			Row(
				modifier = Modifier
					.weight(1f)
					.background(AlturaSearchBg, RoundedCornerShape(20.dp))
					.padding(horizontal = 12.dp, vertical = 8.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(6.dp)
			) {
				Icon(
					Icons.Outlined.Search,
					contentDescription = null,
					tint = AlturaTextSecondary,
					modifier = Modifier.size(18.dp)
				)
				Text(
					text = "Search products...",
					color = AlturaTextSecondary,
					fontSize = 14.sp
				)
			}

			IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
				Icon(Icons.Outlined.CameraAlt, contentDescription = "Scan", tint = Color.Black)
			}
			IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
				Icon(
					Icons.Outlined.Notifications,
					contentDescription = "Notifications",
					tint = Color.Black
				)
			}
		}
	}
}

// ---------------------------------------------------------------------------
// Home catalog layout
// ---------------------------------------------------------------------------

@Composable
private fun HomeCatalogContent(
	vendors: List<Vendor>,
	products: List<Product>,
	onVendorClick: (String) -> Unit,
	onProductClick: (String) -> Unit = {},
	onAddToCart: (Product) -> Unit = {}
) {
	val productsByVendor = products.groupBy { it.vendorId }

	LazyColumn {
		// Hot Picks banner
		item {
			HotPicksBanner()
		}

		// Image carousel with pagination dots
		item {
			BannerCarousel()
		}

		// Vendor sections
		vendors.forEachIndexed { index, vendor ->
			val vendorProducts = productsByVendor[vendor.id].orEmpty()
			if (vendorProducts.isNotEmpty()) {
				item(key = "header_${vendor.id}") {
					VendorSectionHeader(
						vendor = vendor,
						color = sectionHeaderColors[index % sectionHeaderColors.size],
						onClick = { onVendorClick(vendor.id) }
					)
				}
				item(key = "grid_${vendor.id}") {
					ProductGrid(
						products = vendorProducts,
						onProductClick = onProductClick,
						onAddToCart = onAddToCart
					)
				}
			}
		}

		item { Spacer(modifier = Modifier.height(16.dp)) }
	}
}

// ---------------------------------------------------------------------------
// Vendor drill-down layout
// ---------------------------------------------------------------------------

@Composable
private fun VendorProductsContent(
	vendor: Vendor,
	products: List<Product>,
	onProductClick: (String) -> Unit = {},
	onAddToCart: (Product) -> Unit = {}
) {
	LazyColumn {
		item {
			if (vendor.description.isNotBlank()) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.background(AlturaOrange)
						.padding(horizontal = 16.dp, vertical = 10.dp)
				) {
					Text(
						text = vendor.description,
						color = Color.White,
						fontSize = 13.sp
					)
				}
			}
		}
		item {
			ProductGrid(
				products = products,
				onProductClick = onProductClick,
				onAddToCart = onAddToCart
			)
		}
		item { Spacer(modifier = Modifier.height(16.dp)) }
	}
}

// ---------------------------------------------------------------------------
// Reusable components
// ---------------------------------------------------------------------------

@Composable
private fun HotPicksBanner() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(AlturaOrange)
			.padding(horizontal = 16.dp, vertical = 9.dp)
	) {
		Text(
			text = "Hot Picks — Exclusive Deals & Discounts",
			color = Color.White,
			fontWeight = FontWeight.Bold,
			fontSize = 13.sp
		)
	}
}

@Composable
private fun BannerCarousel() {
	val bannerColors = listOf(
		Color(0xFF4FC3F7),
		Color(0xFF81C784),
		Color(0xFFFFB74D)
	)
	var activeDot by remember { mutableIntStateOf(1) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(Color.White)
			.padding(bottom = 12.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(133.dp)
				.background(bannerColors[activeDot])
				.padding(16.dp),
			contentAlignment = Alignment.BottomStart
		) {
			Text(
				text = "Special Offer",
				color = Color.White,
				fontWeight = FontWeight.Bold,
				fontSize = 18.sp
			)
		}

		// Pagination dots
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 8.dp),
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			repeat(6) { index ->
				Box(
					modifier = Modifier
						.padding(horizontal = 4.dp)
						.size(8.dp)
						.clip(CircleShape)
						.background(if (index == activeDot) AlturaCyan else AlturaDotInactive)
						.clickable { activeDot = index }
				)
			}
		}
	}
}

@Composable
private fun VendorSectionHeader(
	vendor: Vendor,
	color: Color,
	onClick: () -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(color)
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 10.dp)
	) {
		Text(
			text = vendor.name,
			color = Color.White,
			fontWeight = FontWeight.Bold,
			fontSize = 14.sp
		)
	}
}

@Composable
private fun ProductGrid(
	products: List<Product>,
	onProductClick: (String) -> Unit = {},
	onAddToCart: (Product) -> Unit = {}
) {
	val rows = products.chunked(3)
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(AlturaBackground)
			.padding(vertical = 4.dp)
	) {
		rows.forEach { rowProducts ->
			Row(modifier = Modifier.fillMaxWidth()) {
				rowProducts.forEach { product ->
					ProductCard(
						product = product,
						onAddToCart = { onAddToCart(product) },
						modifier = Modifier.weight(1f)
							.clickable { onProductClick(product.handle.ifEmpty { product.id }) }
					)
				}
				// Fill empty slots in the last row
				repeat(3 - rowProducts.size) {
					Spacer(modifier = Modifier.weight(1f))
				}
			}
		}
	}
}

@Composable
private fun ProductCard(
	product: Product,
	onAddToCart: (() -> Unit)? = null,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier
			.padding(4.dp)
			.background(Color.White, RoundedCornerShape(8.dp))
			.padding(8.dp)
	) {
		// Product image
		KamelImage(
			resource = asyncPainterResource(data = product.thumbnailUrl ?: product.imageUrl ?: ""),
			contentDescription = product.name,
			modifier = Modifier
				.fillMaxWidth()
				.aspectRatio(1f)
				.clip(RoundedCornerShape(6.dp))
				.background(AlturaBackground),
			contentScale = ContentScale.Crop
		)

		Spacer(modifier = Modifier.height(6.dp))

		Text(
			text = product.name,
			fontSize = 11.sp,
			fontWeight = FontWeight.Medium,
			color = Color.Black,
			maxLines = 2,
			overflow = TextOverflow.Ellipsis
		)

		Spacer(modifier = Modifier.height(2.dp))

		Text(
			text = "${formatTwoDecimals(product.price)} ${product.currency}",
			fontSize = 11.sp,
			fontWeight = FontWeight.Bold,
			color = AlturaBlue
		)

		if (product.variants.isNotEmpty()) {
			Text(
				text = "${product.variants.size} variant${if (product.variants.size > 1) "s" else ""}",
				fontSize = 10.sp,
				color = AlturaGold
			)
		}

		if (onAddToCart != null && product.variants.isNotEmpty()) {
			Spacer(modifier = Modifier.height(4.dp))
			Button(
				onClick = onAddToCart,
				modifier = Modifier.fillMaxWidth(),
				contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
				shape = RoundedCornerShape(6.dp)
			) {
				Icon(
					Icons.Default.ShoppingCart,
					contentDescription = null,
					modifier = Modifier.size(14.dp)
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text("Add to Cart", fontSize = 10.sp)
			}
		}
	}
}
