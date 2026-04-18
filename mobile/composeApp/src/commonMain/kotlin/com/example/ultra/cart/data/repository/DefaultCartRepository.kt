package com.example.ultra.cart.data.repository

import com.example.ultra.core.data.MedusaApiService
import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.CartItem
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.repository.CartRepository

class DefaultCartRepository(
    private val apiService: MedusaApiService
) : CartRepository {
    
    private var currentCartId: String? = null
    
    override suspend fun getCart(): Cart {
        return try {
            if (currentCartId != null) {
                apiService.getCart(currentCartId!!)
            } else {
                val newCart = apiService.createCart()
                currentCartId = newCart.id
                newCart
            }
        } catch (e: Exception) {
            Cart(items = emptyList())
        }
    }
    
    override suspend fun addToCart(product: Product, quantity: Int): Cart {
        return try {
            val cart = getCart()
            val variantId = product.variants.firstOrNull()?.id ?: ""
            if (variantId.isNotEmpty()) {
                val updatedCart = apiService.addToCart(cart.id, variantId, quantity)
                updatedCart
            } else {
                cart
            }
        } catch (e: Exception) {
            throw Exception("Failed to add to cart: ${e.message}")
        }
    }
    
    override suspend fun updateCartItem(cartItemId: String, quantity: Int): Cart {
        return try {
            val cart = getCart()
            if (quantity <= 0) {
                apiService.removeCartItem(cart.id, cartItemId)
            } else {
                apiService.updateCartItem(cart.id, cartItemId, quantity)
            }
        } catch (e: Exception) {
            throw Exception("Failed to update cart item: ${e.message}")
        }
    }
    
    override suspend fun removeFromCart(cartItemId: String): Cart {
        return try {
            val cart = getCart()
            apiService.removeCartItem(cart.id, cartItemId)
        } catch (e: Exception) {
            throw Exception("Failed to remove from cart: ${e.message}")
        }
    }
    
    override suspend fun clearCart(): Cart {
        val cart = getCart()
        for (item in cart.items) {
            try {
                apiService.removeCartItem(cart.id, item.id)
            } catch (e: Exception) {
                // Continue removing other items
            }
        }
        currentCartId = null
        return Cart(items = emptyList())
    }
    
    private data class ProductWithVariants(
        val product: Product,
        val variants: List<String> = emptyList()
    )
}