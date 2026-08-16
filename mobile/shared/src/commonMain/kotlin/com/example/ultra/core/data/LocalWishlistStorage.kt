package com.example.ultra.core.data

import com.example.ultra.core.domain.model.Product
import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalWishlistStorage(private val settings: Settings) {

    companion object {
        private const val KEY_WISHLIST_ITEMS = "local_wishlist_items"
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun getWishlistItems(): List<Product> {
        val itemsJson = settings.getStringOrNull(KEY_WISHLIST_ITEMS) ?: return emptyList()
        return try {
            json.decodeFromString<List<WishlistProductDto>>(itemsJson).map { it.toProduct() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addProduct(product: Product) {
        val items = getWishlistItems().toMutableList()
        if (items.none { it.id == product.id }) {
            items.add(0, product)
            saveItems(items)
        }
    }

    fun removeProduct(productId: String): List<Product> {
        val items = getWishlistItems().filter { it.id != productId }
        saveItems(items)
        return items
    }

    fun isProductWishlisted(productId: String): Boolean {
        return getWishlistItems().any { it.id == productId }
    }

    fun clear() {
        settings.remove(KEY_WISHLIST_ITEMS)
    }

    private fun saveItems(items: List<Product>) {
        val dtos = items.map { it.toWishlistProductDto() }
        settings.putString(KEY_WISHLIST_ITEMS, json.encodeToString(dtos))
    }

    @kotlinx.serialization.Serializable
    private data class WishlistProductDto(
        val id: String,
        val vendorId: String,
        val name: String,
        val description: String,
        val price: Double,
        val oldPrice: Double? = null,
        val currency: String = "NGN",
        val imageUrl: String? = null,
        val thumbnailUrl: String? = null,
        val category: String? = null,
        val inStock: Boolean = true,
        val handle: String = ""
    )

    private fun Product.toWishlistProductDto() = WishlistProductDto(
        id = id,
        vendorId = vendorId,
        name = name,
        description = description,
        price = price,
        oldPrice = oldPrice,
        currency = currency,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        category = category,
        inStock = inStock,
        handle = handle
    )

    private fun WishlistProductDto.toProduct() = Product(
        id = id,
        vendorId = vendorId,
        name = name,
        description = description,
        price = price,
        oldPrice = oldPrice,
        currency = currency,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        category = category,
        inStock = inStock,
        handle = handle
    )
}
