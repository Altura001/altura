package com.example.ultra.category.presentation.intent

sealed interface CategoryIntent {
    data class SelectCategory(val categoryId: String) : CategoryIntent
    data class Search(val query: String) : CategoryIntent
}
