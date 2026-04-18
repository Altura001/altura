package com.example.ultra.core.domain.repository

import com.example.ultra.core.data.MedusaApiService
import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.model.Vendor

interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun logout()
    fun getCurrentUser(): User?
    suspend fun googleAuth(token: String): User
    suspend fun appleAuth(authorizationCode: String): User
    fun isLoggedIn(): Boolean
}

interface CatalogRepository {
    suspend fun getVendors(): List<Vendor>
    suspend fun getAllProducts(): List<Product>
    suspend fun getProductsByVendor(vendorId: String): List<Product>
    suspend fun getProductById(productId: String): Product
    suspend fun getProductByHandle(handle: String): Product?
}

interface CartRepository {
    suspend fun getCart(): Cart
    suspend fun addToCart(product: Product, quantity: Int): Cart
    suspend fun updateCartItem(cartItemId: String, quantity: Int): Cart
    suspend fun removeFromCart(cartItemId: String): Cart
    suspend fun clearCart(): Cart
}
