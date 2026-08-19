package com.example.ultra.shopping.category.presentation.category.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.shopping.category.domain.model.Category
import com.example.ultra.shopping.category.domain.model.CategorySection
import com.example.ultra.shopping.category.domain.model.SubCategory
import com.example.ultra.shopping.category.presentation.intent.CategoryIntent
import com.example.ultra.shopping.category.presentation.intent.CategoryState
import com.example.ultra.shopping.category.viewmodel.CategoryViewModel
import com.example.ultra.core.presentation.theme.AlturaYellow
import com.example.ultra.core.presentation.theme.AlturaSearchBg
import com.example.ultra.core.presentation.theme.AlturaBackground
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoryScreenRoot(viewModel: CategoryViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CategoryScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun CategoryScreen(
    state: CategoryState,
    onAction: (CategoryIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        CategoryTopBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = { onAction(CategoryIntent.Search(it)) }
        )

        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar
            CategorySidebar(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelect = { onAction(CategoryIntent.SelectCategory(it)) },
                modifier = Modifier.width(100.dp).fillMaxHeight()
            )

            // Content
            state.selectedCategory?.let { category ->
                CategoryContent(
                    category = category,
                    modifier = Modifier.fillMaxSize().padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(28.dp).clickable { /* Handle back */ }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(AlturaSearchBg, RoundedCornerShape(24.dp))
                .clickable { onSearchQueryChange(searchQuery) }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = searchQuery.ifEmpty { "Search for Altura" },
                color = Color.Black,
                fontWeight = if (searchQuery.isEmpty()) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CategorySidebar(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.background(AlturaBackground)
    ) {
        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(if (isSelected) AlturaYellow else Color.Transparent)
                    .clickable { onCategorySelect(category.id) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            HorizontalDivider(color = Color.White, thickness = 1.dp)
        }
    }
}

@Composable
private fun CategoryContent(
    category: Category,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AllProductsRow()
        }

        items(category.sections) { section ->
            SubCategorySection(section = section)
        }
    }
}

@Composable
private fun AllProductsRow() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("All Prodct", style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun SubCategorySection(section: CategorySection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            if (section.name == "Home & Kitchen") {
                Text(
                    text = "See All",
                    color = AlturaYellow,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { /* Handle See All */ }
                )
            }
        }
        
        val rows = section.subCategories.chunked(3)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                rowItems.forEach { item ->
                    SubCategoryItem(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SubCategoryItem(
    item: SubCategory,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp).clickable { /* Handle SubCategory click */ },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(AlturaBackground, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 12.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun CategoryScreenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            CategoryScreen(
                state = CategoryState(
                    categories = listOf(
                        Category(id = "1", name = "Homes & Office"),
                        Category(id = "2", name = "Phones & Tablets"),
                        Category(id = "3", name = "Fashion")
                    ),
                    selectedCategoryId = "1"
                ),
                onAction = {}
            )
        }
    }
}
