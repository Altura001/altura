package com.example.ultra.cart.data.repository

import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.data.toCart
import com.example.ultra.core.data.util.safeApiCall
import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.core.domain.util.map

/** CartRepository backed by the Altura Nova .NET backend (requires auth). */
class AlturaCartRepository(
    private val api: AlturaApiService
) : CartRepository {

    override suspend fun getCart(): Result<Cart, DataError.Network> =
        safeApiCall { api.getCart().toCart() }

    override suspend fun addToCart(product: Product, quantity: Int): Result<Cart, DataError.Network> {
        val variantId = product.variants.firstOrNull()?.id
            ?: return safeApiCall { api.getCart().toCart() }
        return safeApiCall { api.addCartItem(variantId, quantity).toCart() }
    }

    override suspend fun updateCartItem(cartItemId: String, quantity: Int): Result<Cart, DataError.Network> =
        safeApiCall { api.updateCartItem(cartItemId, quantity).toCart() }

    override suspend fun removeFromCart(cartItemId: String): Result<Cart, DataError.Network> =
        safeApiCall { api.removeCartItem(cartItemId).toCart() }

    override suspend fun clearCart(): Result<Cart, DataError.Network> =
        safeApiCall { api.clearCart().toCart() }
}
