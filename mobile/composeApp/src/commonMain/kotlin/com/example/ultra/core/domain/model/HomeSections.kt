package com.example.ultra.core.domain.model

enum class HomeSections(
	val id: String,
	val title: String
)
{
	HOT_SALES("hot_sales", "Hot Sales"),
	TOP_SELLING_ITEMS("top_selling_items", "Top Selling Items"),
	HOME_DEALS("home_deals", "Home Deals"),
}