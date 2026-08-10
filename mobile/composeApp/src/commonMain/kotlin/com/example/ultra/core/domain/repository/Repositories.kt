package com.example.ultra.core.domain.repository

import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.AuthAccountType
import com.example.ultra.core.domain.model.Address
import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.model.PaymentInitiation
import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.EmptyResult
import com.example.ultra.core.domain.util.Result

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
    suspend fun getProductById(productId: String): Result<Product, DataError.Network>
    suspend fun getProductByHandle(handle: String): Result<Product?, DataError.Network>
}

interface CartRepository {
    suspend fun getCart(): Result<Cart, DataError.Network>
    suspend fun addToCart(product: Product, quantity: Int): Result<Cart, DataError.Network>
    suspend fun updateCartItem(cartItemId: String, quantity: Int): Result<Cart, DataError.Network>
    suspend fun removeFromCart(cartItemId: String): Result<Cart, DataError.Network>
    suspend fun clearCart(): Result<Cart, DataError.Network>
}

interface OrderRepository {
    suspend fun checkout(address: Address): Result<Order, DataError.Network>
    suspend fun getOrders(): Result<List<Order>, DataError.Network>
    suspend fun getOrder(orderId: String): Result<Order, DataError.Network>
    suspend fun cancelOrder(orderId: String): Result<Order, DataError.Network>
}

interface PaymentRepository {
    suspend fun initiatePayment(
        orderId: String,
        callbackUrl: String?
    ): Result<PaymentInitiation, DataError.Network>

    suspend fun verifyPayment(orderId: String): Result<Order, DataError.Network>
}
