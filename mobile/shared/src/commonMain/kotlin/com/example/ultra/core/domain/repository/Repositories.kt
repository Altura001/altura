package com.example.ultra.core.domain.repository

import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.AuthAccountType
import com.example.ultra.core.domain.model.Address
import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.model.PaymentInitiation
import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.domain.model.PickupStation
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.EmptyResult
import com.example.ultra.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
        accountType: AuthAccountType = AuthAccountType.CUSTOMER
    ): Result<User, DataError.Network>
    suspend fun signupCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<User, DataError.Network>
    suspend fun signupVendor(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        storeName: String
    ): Result<User, DataError.Network>
    suspend fun logout(): EmptyResult<DataError.Network>
    fun getCurrentUser(): User?
    suspend fun googleAuth(token: String): Result<User, DataError.Network>
    suspend fun appleAuth(authorizationCode: String): Result<User, DataError.Network>
    fun isLoggedIn(): Boolean
}

interface CatalogRepository {
    suspend fun getVendors(): Result<List<Vendor>, DataError.Network>
    suspend fun getAllProducts(): Result<List<Product>, DataError.Network>
    suspend fun getProductsByVendor(vendorId: String): Result<List<Product>, DataError.Network>
    suspend fun searchProducts(query: String): Result<List<Product>, DataError.Network>
    suspend fun getProductById(productId: String): Result<Product, DataError.Network>
    suspend fun getProductByHandle(handle: String): Result<Product?, DataError.Network>
    suspend fun getPickupStations(): Result<List<PickupStation>, DataError.Network>
}

interface CartRepository {
    suspend fun getCart(): Result<Cart, DataError>
    suspend fun addToCart(product: Product, quantity: Int): Result<Cart, DataError>
    suspend fun updateCartItem(cartItemId: String, quantity: Int): Result<Cart, DataError>
    suspend fun removeFromCart(cartItemId: String): Result<Cart, DataError>
    suspend fun clearCart(): Result<Cart, DataError>
    fun observeCart(): Flow<Cart>
    suspend fun mergeLocalCartWithServer()
}

interface OrderRepository {
    suspend fun checkout(address: Address, deliveryMethod: String? = null, pickupStationId: String? = null, items: List<com.example.ultra.core.data.CheckoutItemDto>? = null, email: String? = null): Result<Order, DataError.Network>
    suspend fun getOrders(): Result<List<Order>, DataError.Network>
    suspend fun getOrder(orderId: String): Result<Order, DataError.Network>
    suspend fun cancelOrder(orderId: String): Result<Order, DataError.Network>
}

interface WishlistRepository {
    suspend fun getWishlist(): Result<List<Product>, DataError>
    suspend fun addToWishlist(product: Product): Result<List<Product>, DataError>
    suspend fun removeFromWishlist(productId: String): Result<List<Product>, DataError>
    suspend fun toggleWishlist(product: Product): Result<List<Product>, DataError>
    fun isProductWishlisted(productId: String): Boolean
    fun observeWishlist(): Flow<List<Product>>
}

interface PaymentRepository {
    suspend fun initiatePayment(
        orderId: String,
        callbackUrl: String?
    ): Result<PaymentInitiation, DataError.Network>

    suspend fun verifyPayment(orderId: String): Result<Order, DataError.Network>
}
