package com.example.ultra.shopping.category.domain.model

data class Category(
    val id: String,
    val name: String,
    val icon: String? = null,
    val sections: List<CategorySection> = emptyList()
)

data class CategorySection(
    val id: String,
    val name: String,
    val subCategories: List<SubCategory> = emptyList()
)

data class SubCategory(
    val id: String,
    val name: String,
    val imageUrl: String? = null
)
