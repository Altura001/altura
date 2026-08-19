package com.example.ultra.shopping.cart.data.repository

import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.data.LocalCartStorage
import com.example.ultra.core.data.toCart
import com.example.ultra.core.data.util.safeApiCall
import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class DefaultCartRepository(
    private val api: AlturaApiService,
    private val authRepository: AuthRepository,
    private val localCartStorage: LocalCartStorage
) : CartRepository {

    private val _localCart = MutableStateFlow(Cart())

    init {
        _localCart.value = localCartStorage.getCart()
    }

    override fun observeCart(): Flow<Cart> {
        return _localCart.asStateFlow()
    }

    override suspend fun getCart(): Result<Cart, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall { api.getCart().toCart() }
        } else {
            val cart = localCartStorage.getCart()
            _localCart.value = cart
            Result.Success(cart)
        }
    }

    override suspend fun addToCart(product: Product, quantity: Int): Result<Cart, DataError> {
        return if (authRepository.isLoggedIn()) {
            val variantId = product.variants.firstOrNull()?.id
                ?: return safeApiCall { api.getCart().toCart() }
            safeApiCall { api.addCartItem(variantId, quantity).toCart() }
        } else {
            localCartStorage.addItem(product, quantity)
            val cart = localCartStorage.getCart()
            _localCart.value = cart
            Result.Success(cart)
        }
    }

    override suspend fun updateCartItem(cartItemId: String, quantity: Int): Result<Cart, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall { api.updateCartItem(cartItemId, quantity).toCart() }
        } else {
            val updatedCart = localCartStorage.updateItemQuantity(cartItemId, quantity)
            _localCart.value = updatedCart
            Result.Success(updatedCart)
        }
    }

    override suspend fun removeFromCart(cartItemId: String): Result<Cart, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall { api.removeCartItem(cartItemId).toCart() }
        } else {
            val updatedCart = localCartStorage.removeItem(cartItemId)
            _localCart.value = updatedCart
            Result.Success(updatedCart)
        }
    }

    override suspend fun clearCart(): Result<Cart, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall { api.clearCart().toCart() }
        } else {
            localCartStorage.clear()
            _localCart.value = Cart()
            Result.Success(Cart())
        }
    }

    override suspend fun mergeLocalCartWithServer() {
        val localItems = localCartStorage.getCart().items
        if (localItems.isEmpty()) return

        for (item in localItems) {
            item.product?.let { product ->
                val variantId = product.variants.firstOrNull()?.id ?: return@let
                safeApiCall { api.addCartItem(variantId, item.quantity) }
            }
        }

        localCartStorage.clear()
        _localCart.value = Cart()
    }
}
