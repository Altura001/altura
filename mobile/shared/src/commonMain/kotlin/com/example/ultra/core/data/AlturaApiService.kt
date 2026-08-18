package com.example.ultra.core.data

import com.example.ultra.core.domain.model.BirdImage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Raised when the backend responds with a non-success status. */
class ApiException(val statusCode: Int, message: String? = null) :
	RuntimeException(message ?: "HTTP $statusCode")

/**
 * HTTP client for the Altura Nova .NET backend. Attaches the JWT bearer token to
 * authenticated calls and transparently refreshes it once on a 401.
 */
class AlturaApiService(
	private val httpClient: HttpClient,
	private val baseUrl: String,
	private val tokenStorage: TokenStorage
) {
	private val api = "$baseUrl/api"

	// ----- Auth --------------------------------------------------------------

	suspend fun login(email: String, password: String): AuthResponse {
		println(baseUrl)
		return post("$api/auth/login", LoginRequest(email, password)).decode<AuthResponse>()
	}

	suspend fun registerCustomer(
		email: String, password: String, firstName: String, lastName: String, phone: String?
	): AuthResponse =
		post(
			"$api/auth/register/customer",
			RegisterCustomerRequest(email, password, firstName, lastName, phone)
		).decode()

	suspend fun registerVendor(
		email: String,
		password: String,
		firstName: String,
		lastName: String,
		storeName: String,
		phone: String?
	): AuthResponse =
		post(
			"$api/auth/register/vendor",
			RegisterVendorRequest(email, password, firstName, lastName, storeName, phone)
		).decode()

	suspend fun refresh(refreshToken: String): AuthResponse =
		post("$api/auth/refresh", RefreshRequest(refreshToken)).decode()

	suspend fun logout(refreshToken: String) {
		authorized(HttpMethod.Post, "$api/auth/logout", RefreshRequest(refreshToken)).let {
			if (!it.status.isSuccess() && it.status != HttpStatusCode.NoContent) {
				// best-effort; ignore
			}
		}
	}

	suspend fun me(): UserDto = authorized(HttpMethod.Get, "$api/auth/me").decode()

	// ----- Account -----------------------------------------------------------

	suspend fun updateProfile(firstName: String, lastName: String, phone: String?): UserDto =
		authorized(
			HttpMethod.Put, "$api/account/profile",
			UpdateProfileRequest(firstName, lastName, phone)
		).decode()

	suspend fun changePassword(currentPassword: String, newPassword: String) {
		authorized(
			HttpMethod.Post, "$api/account/change-password",
			ChangePasswordRequest(currentPassword, newPassword)
		).ensureSuccess()
	}

	// ----- Catalog (public) --------------------------------------------------

	suspend fun getVendors(): List<VendorDto> = get("$api/vendors").decode()

	suspend fun getCategories(): List<CategoryDto> = get("$api/categories").decode()

	suspend fun getProducts(
		search: String? = null,
		vendorId: String? = null,
		categoryId: String? = null,
		page: Int = 1,
		pageSize: Int = 50
	): ProductListDto = get("$api/products") {
		url {
			search?.let { parameters.append("search", it) }
			vendorId?.let { parameters.append("vendorId", it) }
			categoryId?.let { parameters.append("categoryId", it) }
			parameters.append("page", page.toString())
			parameters.append("pageSize", pageSize.toString())
		}
	}.decode()

	suspend fun getVendorProducts(vendorId: String): ProductListDto =
		get("$api/vendors/$vendorId/products").decode()

	suspend fun getProductById(id: String): ProductDto = get("$api/products/$id").decode()

	suspend fun getProductByHandle(handle: String): ProductDto =
		get("$api/products/handle/$handle").decode()

	// ----- Cart (auth) -------------------------------------------------------

	suspend fun getCart(): CartDto = authorized(HttpMethod.Get, "$api/cart").decode()

	suspend fun addCartItem(variantId: String, quantity: Int): CartDto =
		authorized(
			HttpMethod.Post,
			"$api/cart/items",
			AddCartItemRequest(variantId, quantity)
		).decode()

	suspend fun updateCartItem(itemId: String, quantity: Int): CartDto =
		authorized(
			HttpMethod.Patch,
			"$api/cart/items/$itemId",
			UpdateCartItemRequest(quantity)
		).decode()

	suspend fun removeCartItem(itemId: String): CartDto =
		authorized(HttpMethod.Delete, "$api/cart/items/$itemId").decode()

	suspend fun clearCart(): CartDto = authorized(HttpMethod.Delete, "$api/cart").decode()

	// ----- Wishlist (auth) ---------------------------------------------------

	suspend fun getWishlist(): WishlistResponseDto =
		authorized(HttpMethod.Get, "$api/wishlist").decode()

	suspend fun addToWishlist(productId: String): WishlistResponseDto =
		authorized(HttpMethod.Post, "$api/wishlist", AddWishlistRequest(productId)).decode()

	suspend fun removeFromWishlist(productId: String): WishlistResponseDto =
		authorized(HttpMethod.Delete, "$api/wishlist/$productId").decode()

	suspend fun toggleWishlist(productId: String): WishlistResponseDto =
		authorized(HttpMethod.Put, "$api/wishlist/toggle/$productId").decode()

	// ----- Pickup Stations (public) -----------------------------------------

	suspend fun getPickupStations(): List<PickupStationDto> =
		get("$api/pickup-stations").decode()

	// ----- Orders + payment (auth) ------------------------------------------

	suspend fun checkout(address: AddressDto, deliveryMethod: String? = null, pickupStationId: String? = null): OrderDto =
		authorized(HttpMethod.Post, "$api/orders/checkout", CheckoutRequest(address, deliveryMethod, pickupStationId)).decode()

	suspend fun getOrders(): OrderListDto = authorized(HttpMethod.Get, "$api/orders").decode()

	suspend fun getOrder(id: String): OrderDto =
		authorized(HttpMethod.Get, "$api/orders/$id").decode()

	suspend fun cancelOrder(id: String): OrderDto =
		authorized(HttpMethod.Post, "$api/orders/$id/cancel").decode()

	suspend fun initiatePayment(orderId: String, callbackUrl: String?): PaymentInitiationDto =
		authorized(
			HttpMethod.Post,
			"$api/orders/$orderId/pay",
			InitiatePaymentRequest(callbackUrl)
		).decode()

	suspend fun verifyPayment(orderId: String): OrderDto =
		authorized(HttpMethod.Post, "$api/orders/$orderId/verify").decode()

	// ----- request plumbing --------------------------------------------------

	val httpClient2 = HttpClient {
		install(ContentNegotiation) {
			json()
		}
	}


	private suspend fun get(url: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
		httpClient.get(url, block)

	private suspend fun post(url: String, body: Any): HttpResponse =
		httpClient.request(url) {
			method = HttpMethod.Post
			contentType(ContentType.Application.Json)
			setBody(body)
		}

	/** Executes an authenticated request, refreshing the token once on a 401. */
	private suspend fun authorized(
		httpMethod: HttpMethod,
		url: String,
		body: Any? = null
	): HttpResponse {
		val first = executeWithToken(httpMethod, url, body, tokenStorage.getToken())
		if (first.status != HttpStatusCode.Unauthorized) return first

		return if (tryRefresh()) {
			executeWithToken(httpMethod, url, body, tokenStorage.getToken())
		} else {
			first
		}
	}

	private suspend fun executeWithToken(
		httpMethod: HttpMethod,
		url: String,
		body: Any?,
		token: String?
	): HttpResponse = httpClient.request(url) {
		method = httpMethod
		token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
		if (body != null) {
			contentType(ContentType.Application.Json)
			setBody(body)
		}
	}

	private suspend fun tryRefresh(): Boolean {
		val refreshToken = tokenStorage.getRefreshToken() ?: return false
		return try {
			val response = post("$api/auth/refresh", RefreshRequest(refreshToken))
			if (!response.status.isSuccess()) return false
			val auth: AuthResponse = response.body()
			tokenStorage.saveTokens(auth.accessToken, auth.refreshToken, 0L)
			true
		} catch (_: Exception) {
			false
		}
	}

	private fun HttpResponse.ensureSuccess() {
		if (!status.isSuccess()) throw ApiException(status.value)
	}

	private suspend inline fun <reified T> HttpResponse.decode(): T {
		if (!status.isSuccess()) throw ApiException(status.value)
		return body()
	}
}

// ---------------------------------------------------------------------------
// Wire DTOs (match the .NET backend JSON contract, camelCase)
// ---------------------------------------------------------------------------

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterCustomerRequest(
	val email: String,
	val password: String,
	val firstName: String,
	val lastName: String,
	val phone: String?
)

@Serializable
data class RegisterVendorRequest(
	val email: String, val password: String, val firstName: String, val lastName: String,
	val storeName: String, val phone: String?
)

@Serializable
data class RefreshRequest(val refreshToken: String)

@Serializable
data class UpdateProfileRequest(val firstName: String, val lastName: String, val phone: String?)

@Serializable
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)

@Serializable
data class AddCartItemRequest(val variantId: String, val quantity: Int)

@Serializable
data class UpdateCartItemRequest(val quantity: Int)

@Serializable
data class CheckoutRequest(
	val shippingAddress: AddressDto,
	val deliveryMethod: String? = null,
	val pickupStationId: String? = null
)

@Serializable
data class InitiatePaymentRequest(val callbackUrl: String?)

@Serializable
data class AddWishlistRequest(val productId: String)

@Serializable
data class AuthResponse(
	val user: UserDto,
	val accessToken: String,
	val refreshToken: String,
	val accessTokenExpiresAt: String
)

@Serializable
data class UserDto(
	val id: String,
	val email: String,
	val firstName: String,
	val lastName: String,
	val phone: String? = null,
	val role: String,
	val vendorId: String? = null
)

@Serializable
data class VendorDto(
	val id: String,
	val name: String,
	val handle: String,
	val description: String = "",
	val logoUrl: String? = null,
	val bannerUrl: String? = null
)

@Serializable
data class CategoryDto(val id: String, val name: String, val handle: String)

@Serializable
data class ProductVariantDto(
	val id: String,
	val title: String,
	val sku: String? = null,
	val price: Double,
	val currency: String = "EUR",
	val inventoryQuantity: Int = 0,
	val isInStock: Boolean = false
)

@Serializable
data class ProductDto(
	val id: String,
	val vendorId: String,
	val vendorName: String = "",
	val name: String,
	val handle: String = "",
	val description: String = "",
	val price: Double = 0.0,
	val currency: String = "EUR",
	val thumbnailUrl: String? = null,
	val images: List<String> = emptyList(),
	val category: String? = null,
	val inStock: Boolean = true,
	val variants: List<ProductVariantDto> = emptyList()
)

@Serializable
data class ProductListDto(
	val items: List<ProductDto> = emptyList(),
	val total: Int = 0,
	val page: Int = 1,
	val pageSize: Int = 50
)

@Serializable
data class CartItemDto(
	val id: String,
	val productId: String,
	val variantId: String,
	val title: String = "",
	val quantity: Int = 0,
	val unitPrice: Double = 0.0,
	val lineTotal: Double = 0.0,
	val currency: String = "EUR",
	val thumbnailUrl: String? = null
)

@Serializable
data class CartDto(
	val id: String = "",
	val items: List<CartItemDto> = emptyList(),
	val subtotal: Double = 0.0,
	val total: Double = 0.0,
	val currency: String = "EUR",
	val itemCount: Int = 0
)

@Serializable
data class AddressDto(
	val firstName: String,
	val lastName: String,
	val line1: String,
	val line2: String? = null,
	val city: String,
	val state: String? = null,
	val postalCode: String,
	val country: String,
	val phone: String? = null
)

@Serializable
data class OrderItemDto(
	val id: String,
	val productId: String,
	val variantId: String,
	val productName: String,
	val sku: String? = null,
	val thumbnailUrl: String? = null,
	val unitPrice: Double = 0.0,
	val quantity: Int = 0,
	val lineTotal: Double = 0.0,
	val currency: String = "EUR"
)

@Serializable
data class OrderDto(
	val id: String,
	val status: String,
	val deliveryMethod: String = "Shipping",
	val pickupStationName: String? = null,
	val subtotal: Double = 0.0,
	val shippingFee: Double = 0.0,
	val total: Double = 0.0,
	val currency: String = "EUR",
	val shippingAddress: AddressDto? = null,
	val items: List<OrderItemDto> = emptyList(),
	val createdAt: String = ""
)

@Serializable
data class OrderListDto(val items: List<OrderDto> = emptyList(), val total: Int = 0)

@Serializable
data class PaymentInitiationDto(
	val orderId: String,
	val provider: String,
	val authorizationUrl: String,
	val accessCode: String,
	val reference: String,
	val publicKey: String,
	val amountSubunits: Long,
	val currency: String
)

@Serializable
data class WishlistItemResponseDto(
	val id: String,
	val productId: String,
	val productName: String = "",
	val thumbnailUrl: String? = null,
	val price: Double = 0.0,
	val currency: String = "EUR",
	val inStock: Boolean = true,
	val addedAt: String = ""
)

@Serializable
data class WishlistResponseDto(
	val userId: String,
	val items: List<WishlistItemResponseDto> = emptyList(),
	val itemCount: Int = 0
)

@Serializable
data class PickupStationDto(
	val id: String,
	val name: String,
	val address: String,
	val city: String,
	val phone: String? = null,
	val operatingHours: String? = null
)
