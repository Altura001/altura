package com.example.ultra.shopping.category.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ultra.shopping.category.domain.model.Category
import com.example.ultra.shopping.category.domain.model.CategorySection
import com.example.ultra.shopping.category.domain.model.SubCategory
import com.example.ultra.shopping.category.presentation.intent.CategoryIntent
import com.example.ultra.shopping.category.presentation.intent.CategoryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    init {
        // Initialize with mock data to match the screenshot
        val mockCategories = listOf(
            Category(
                id = "1",
                name = "Homes & Office",
                sections = listOf(
                    CategorySection(
                        id = "1-1",
                        name = "Appliances",
                        subCategories = listOf(
                            SubCategory(id = "1-1-1", name = "Large Appliances"),
                            SubCategory(id = "1-1-2", name = "Small Appliances")
                        )
                    ),
                    CategorySection(
                        id = "1-2",
                        name = "Home & Kitchen",
                        subCategories = listOf(
                            SubCategory(id = "1-2-1", name = "Cookware"),
                            SubCategory(id = "1-2-2", name = "Small Appliances"),
                            SubCategory(id = "1-2-3", name = "Bakeware"),
                            SubCategory(id = "1-2-4", name = "Cutlery & Knife Accessories")
                        )
                    ),
                    CategorySection(
                        id = "1-3",
                        name = "Home",
                        subCategories = listOf(
                            SubCategory(id = "1-3-1", name = "Bedding"),
                            SubCategory(id = "1-3-2", name = "Home Decor"),
                            SubCategory(id = "1-3-3", name = "Kitchen & Dinner"),
                            SubCategory(id = "1-3-4", name = "Lightening"),
                            SubCategory(id = "1-3-5", name = "Stationery"),
                            SubCategory(id = "1-3-6", name = "Storage & Organization"),
                            SubCategory(id = "1-3-7", name = "Bath"),
                            SubCategory(id = "1-3-8", name = "Wall Art"),
                            SubCategory(id = "1-3-9", name = "Vacuums & Floor Care"),
                            SubCategory(id = "1-3-10", name = "Home Furniture"),
                            SubCategory(id = "1-3-11", name = "Arts, Crafts & Sweing")
                        )
                    )
                )
            ),
            Category(id = "2", name = "Phones & Tablets"),
            Category(id = "3", name = "Fashion"),
            Category(id = "4", name = "Health & Beauty"),
            Category(id = "5", name = "Electronics"),
            Category(id = "6", name = "Computing"),
            Category(id = "7", name = "Garden & Outdoors"),
            Category(id = "8", name = "Automobile"),
            Category(id = "9", name = "Sporting Goods"),
            Category(id = "10", name = "Gaming"),
            Category(id = "11", name = "Baby Products")
        )

        _state.update {
            it.copy(
                categories = mockCategories,
                selectedCategoryId = "1"
            )
        }
    }

    fun onAction(action: CategoryIntent) {
        when (action) {
            is CategoryIntent.SelectCategory -> {
                _state.update { it.copy(selectedCategoryId = action.categoryId) }
            }
            is CategoryIntent.Search -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
        }
    }
}
