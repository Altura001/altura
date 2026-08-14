package com.example.ultra.category.presentation.intent

import com.example.ultra.category.domain.model.Category

data class CategoryState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = ""
) {
    val selectedCategory: Category? = categories.find { it.id == selectedCategoryId }
}

