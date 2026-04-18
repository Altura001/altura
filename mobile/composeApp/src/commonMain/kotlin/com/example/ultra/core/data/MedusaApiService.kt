package com.example.ultra.core.data

import android.util.Log
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.CartItem
import com.example.ultra.core.domain.model.ProductVariant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

class MedusaApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val publishableApiKey: String
) {
    companion object {
        private const val TAG = "MedusaApi"
    }

    suspend fun getProducts(): List<Product> {
        return try {
            Log.d(TAG, "Fetching products from: $baseUrl/store/products")
            val response = httpClient.get("$baseUrl/store/products") {
                header("x-publishable-api-key", publishableApiKey)
            }
            val products = response.body<StoreProductsResponse>().products.map { it.toProduct() }
            Log.d(TAG, "Got ${products.size} products")
            products
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProductByHandle(handle: String): Product? {
        return try {
            httpClient.get("$baseUrl/store/products/$handle") {
                header("x-publishable-api-key", publishableApiKey)
            }.body<StoreProductResponse>().product?.toProduct()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createCart(regionId: String? = null): Cart {
        val body = if (regionId != null) """{"region_id":"$regionId"}""" else "{}"
        return httpClient.post("$baseUrl/store/carts") {
            header("x-publishable-api-key", publishableApiKey)
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<CartResponse>().cart.toCart()
    }

    suspend fun getCart(cartId: String): Cart {
        return httpClient.get("$baseUrl/store/carts/$cartId") {
            header("x-publishable-api-key", publishableApiKey)
        }.body<CartResponse>().cart.toCart()
    }

    suspend fun addToCart(cartId: String, variantId: String, quantity: Int): Cart {
        val body = """{"variant_id":"$variantId","quantity":$quantity}"""
        return httpClient.post("$baseUrl/store/carts/$cartId/line-items") {
            header("x-publishable-api-key", publishableApiKey)
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<CartResponse>().cart.toCart()
    }

    suspend fun updateCartItem(cartId: String, lineItemId: String, quantity: Int): Cart {
        val body = """{"quantity":$quantity}"""
        return httpClient.post("$baseUrl/store/carts/$cartId/line-items/$lineItemId") {
            header("x-publishable-api-key", publishableApiKey)
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<CartResponse>().cart.toCart()
    }

    suspend fun removeCartItem(cartId: String, lineItemId: String): Cart {
        return httpClient.post("$baseUrl/store/carts/$cartId/line-items/$lineItemId/delete") {
            header("x-publishable-api-key", publishableApiKey)
        }.body<CartResponse>().cart.toCart()
    }

    suspend fun createCustomer(email: String, firstName: String, lastName: String, password: String): CustomerResponse {
        val body = """{"email":"$email","first_name":"$firstName","last_name":"$lastName","password":"$password"}"""
        return httpClient.post("$baseUrl/store/customers") {
            header("x-publishable-api-key", publishableApiKey)
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<CustomerResponse>()
    }

    suspend fun login(email: String, password: String): AuthResponse {
        val body = """{"email":"$email","password":"$password"}"""
        return httpClient.post("$baseUrl/store/auth/emailpass") {
            header("x-publishable-api-key", publishableApiKey)
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<AuthResponse>()
    }

    private fun MedusaProduct.toProduct(): Product {
        return Product(
            id = id,
            vendorId = "",
            name = title,
            description = description ?: "",
            price = variants.firstOrNull()?.prices?.firstOrNull()?.amount?.div(100.0) ?: 0.0,
            currency = variants.firstOrNull()?.prices?.firstOrNull()?.currency_code?.uppercase() ?: "USD",
            imageUrl = images.firstOrNull()?.url,
            thumbnailUrl = thumbnail ?: images.firstOrNull()?.url,
            category = categories.firstOrNull()?.name,
            inStock = variants.any { (it.inventory_quantity ?: 0) > 0 },
            variants = variants.map { it.toVariant() },
            handle = handle ?: ""
        )
    }

    private fun MedusaVariant.toVariant(): ProductVariant {
        return ProductVariant(
            id = id,
            title = title,
            sku = sku,
            price = prices.firstOrNull()?.amount?.div(100.0) ?: 0.0,
            currency = prices.firstOrNull()?.currency_code?.uppercase() ?: "EUR",
            inventoryQuantity = inventory_quantity ?: 0
        )
    }

    private fun MedusaCart.toCart(): Cart {
        return Cart(
            id = id,
            items = lineItems.map { it.toCartItem() },
            subtotal = subtotal?.div(100.0) ?: 0.0,
            total = total?.div(100.0) ?: 0.0,
            currency = currency_code ?: "EUR"
        )
    }

    private fun MedusaLineItem.toCartItem(): CartItem {
        return CartItem(
            id = id,
            productId = product_id,
            variantId = variant_id ?: "",
            title = title,
            quantity = quantity,
            unitPrice = unit_price?.div(100.0) ?: 0.0,
            total = (unit_price?.times(quantity))?.div(100.0) ?: 0.0,
            currency = currency_code ?: "EUR",
            imageUrl = thumbnail ?: ""
        )
    }
}

@Serializable
data class StoreProductsResponse(
    val products: List<MedusaProduct> = emptyList(),
    val count: Int = 0
)

@Serializable
data class StoreProductResponse(
    val product: MedusaProduct? = null
)

@Serializable
data class CartResponse(
    val cart: MedusaCart
)

@Serializable
data class AuthResponse(
    val customer: MedusaCustomer? = null,
    val access_token: String? = null
)

@Serializable
data class CustomerResponse(
    val customer: MedusaCustomer
)

@Serializable
data class MedusaProduct(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val handle: String? = null,
    val thumbnail: String? = null,
    val images: List<MedusaImage> = emptyList(),
    val variants: List<MedusaVariant> = emptyList(),
    val options: List<MedusaOption> = emptyList(),
    val categories: List<MedusaCategory> = emptyList()
)

@Serializable
data class MedusaImage(
    val id: String = "",
    val url: String = "",
    val thumbnail: String? = null
)

@Serializable
data class MedusaVariant(
    val id: String = "",
    val title: String = "",
    val sku: String? = null,
    val prices: List<MedusaPrice> = emptyList(),
    val inventory_quantity: Int? = null
)

@Serializable
data class MedusaPrice(
    val amount: Double = 0.0,
    val currency_code: String = "EUR"
)

@Serializable
data class MedusaOption(
    val id: String = "",
    val title: String = ""
)

@Serializable
data class MedusaCategory(
    val id: String = "",
    val name: String = ""
)

@Serializable
data class MedusaCart(
    val id: String = "",
    val email: String? = null,
    val currency_code: String? = null,
    val subtotal: Double? = null,
    val total: Double? = null,
    val lineItems: List<MedusaLineItem> = emptyList()
)

@Serializable
data class MedusaLineItem(
    val id: String = "",
    val title: String = "",
    val product_id: String = "",
    val variant_id: String? = null,
    val quantity: Int = 0,
    val unit_price: Double = 0.0,
    val total: Double = 0.0,
    val currency_code: String = "EUR",
    val thumbnail: String? = null,
    val variant: MedusaCartVariant? = null
)

@Serializable
data class MedusaCartVariant(
    val id: String = "",
    val title: String = ""
)

@Serializable
data class MedusaCustomer(
    val id: String = "",
    val email: String = "",
    val first_name: String = "",
    val last_name: String = ""
)