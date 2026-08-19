package com.example.ultra.core.data

import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.CartItem
import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class LocalCartStorage(private val settings: Settings) {

    companion object {
        private const val KEY_CART_ITEMS = "local_cart_items"
        private const val KEY_CART_ID = "local_cart_id"
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun getCart(): Cart {
        val itemsJson = settings.getStringOrNull(KEY_CART_ITEMS) ?: return Cart()
        return try {
            val items = json.decodeFromString<List<CartItemDto>>(itemsJson)
            val cartId = settings.getStringOrNull(KEY_CART_ID) ?: "local_cart"
            Cart(
                id = cartId,
                items = items.map { it.toCartItem() },
                subtotal = items.sumOf { it.unitPrice * it.quantity },
                total = items.sumOf { it.unitPrice * it.quantity },
                currency = "NGN"
            )
        } catch (_: Exception) {
            Cart()
        }
    }

    fun saveCart(cart: Cart) {
        val items = cart.items.map { it.toCartItemDto() }
        settings.putString(KEY_CART_ITEMS, json.encodeToString(items))
        settings.putString(KEY_CART_ID, cart.id)
    }

    fun addItem(product: com.example.ultra.core.domain.model.Product, quantity: Int) {
        val cart = getCart()
        val variant = product.variants.firstOrNull()
        val existingIndex = cart.items.indexOfFirst {
            it.productId == product.id && it.variantId == (variant?.id ?: "")
        }

        val updatedItems = if (existingIndex >= 0) {
            val existing = cart.items[existingIndex]
            cart.items.toMutableList().apply {
                set(existingIndex, existing.copy(quantity = existing.quantity + quantity))
            }
        } else {
            cart.items + CartItem(
                id = "local_${product.id}_${variant?.id ?: ""}_${Clock.System.now().toEpochMilliseconds()}",
                productId = product.id,
                variantId = variant?.id ?: "",
                title = product.name,
                product = product,
                quantity = quantity,
                unitPrice = variant?.price ?: product.price,
                total = (variant?.price ?: product.price) * quantity,
                currency = product.currency,
                imageUrl = product.thumbnailUrl ?: product.imageUrl
            )
        }

        val updatedCart = cart.copy(
            items = updatedItems,
            subtotal = updatedItems.sumOf { it.unitPrice * it.quantity },
            total = updatedItems.sumOf { it.unitPrice * it.quantity }
        )
        saveCart(updatedCart)
    }

    fun updateItemQuantity(cartItemId: String, quantity: Int): Cart {
        val cart = getCart()
        val updatedItems = if (quantity <= 0) {
            cart.items.filter { it.id != cartItemId }
        } else {
            cart.items.map {
                if (it.id == cartItemId) it.copy(quantity = quantity, total = it.unitPrice * quantity) else it
            }
        }
        val updatedCart = cart.copy(
            items = updatedItems,
            subtotal = updatedItems.sumOf { it.unitPrice * it.quantity },
            total = updatedItems.sumOf { it.unitPrice * it.quantity }
        )
        saveCart(updatedCart)
        return updatedCart
    }

    fun removeItem(cartItemId: String): Cart {
        val cart = getCart()
        val updatedItems = cart.items.filter { it.id != cartItemId }
        val updatedCart = cart.copy(
            items = updatedItems,
            subtotal = updatedItems.sumOf { it.unitPrice * it.quantity },
            total = updatedItems.sumOf { it.unitPrice * it.quantity }
        )
        saveCart(updatedCart)
        return updatedCart
    }

    fun clear() {
        settings.remove(KEY_CART_ITEMS)
        settings.remove(KEY_CART_ID)
    }

    @kotlinx.serialization.Serializable
    private data class CartItemDto(
        val id: String,
        val productId: String,
        val variantId: String = "",
        val title: String = "",
        val quantity: Int = 1,
        val unitPrice: Double = 0.0,
        val total: Double = 0.0,
        val currency: String = "NGN",
        val imageUrl: String? = null,
        val productName: String = "",
        val productDescription: String = "",
        val productVendorId: String = "",
        val productPrice: Double = 0.0,
        val productCurrency: String = "NGN",
        val productImageUrl: String? = null,
        val productThumbnailUrl: String? = null,
        val productInStock: Boolean = true,
        val productHandle: String = ""
    )

    private fun CartItem.toCartItemDto() = CartItemDto(
        id = id,
        productId = productId,
        variantId = variantId,
        title = title,
        quantity = quantity,
        unitPrice = unitPrice,
        total = total,
        currency = currency,
        imageUrl = imageUrl,
        productName = product?.name ?: "",
        productDescription = product?.description ?: "",
        productVendorId = product?.vendorId ?: "",
        productPrice = product?.price ?: 0.0,
        productCurrency = product?.currency ?: "NGN",
        productImageUrl = product?.imageUrl,
        productThumbnailUrl = product?.thumbnailUrl,
        productInStock = product?.inStock ?: true,
        productHandle = product?.handle ?: ""
    )

    private fun CartItemDto.toCartItem() = CartItem(
        id = id,
        productId = productId,
        variantId = variantId,
        title = title,
        quantity = quantity,
        unitPrice = unitPrice,
        total = total,
        currency = currency,
        imageUrl = imageUrl,
        product = if (productName.isNotBlank()) {
            com.example.ultra.core.domain.model.Product(
                id = productId,
                vendorId = productVendorId,
                name = productName,
                description = productDescription,
                price = productPrice,
                currency = productCurrency,
                imageUrl = productImageUrl,
                thumbnailUrl = productThumbnailUrl,
                inStock = productInStock,
                handle = productHandle
            )
        } else null
    )
}
