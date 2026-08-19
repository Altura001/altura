package com.example.ultra.shopping.wishlist.data.repository

import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.data.LocalWishlistStorage
import com.example.ultra.core.data.util.safeApiCall
import com.example.ultra.core.data.toProduct
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.repository.WishlistRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultWishlistRepository(
    private val api: AlturaApiService,
    private val authRepository: AuthRepository,
    private val localWishlistStorage: LocalWishlistStorage
) : WishlistRepository {

    private val _localWishlist = MutableStateFlow<List<Product>>(emptyList())

    init {
        _localWishlist.value = localWishlistStorage.getWishlistItems()
    }

    override suspend fun getWishlist(): Result<List<Product>, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall {
                api.getWishlist().items.map { it.toProduct() }
            }
        } else {
            val items = localWishlistStorage.getWishlistItems()
            _localWishlist.value = items
            Result.Success(items)
        }
    }

    override suspend fun addToWishlist(product: Product): Result<List<Product>, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall {
                api.addToWishlist(product.id).items.map { it.toProduct() }
            }
        } else {
            localWishlistStorage.addProduct(product)
            val items = localWishlistStorage.getWishlistItems()
            _localWishlist.value = items
            Result.Success(items)
        }
    }

    override suspend fun removeFromWishlist(productId: String): Result<List<Product>, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall {
                api.removeFromWishlist(productId).items.map { it.toProduct() }
            }
        } else {
            val items = localWishlistStorage.removeProduct(productId)
            _localWishlist.value = items
            Result.Success(items)
        }
    }

    override suspend fun toggleWishlist(product: Product): Result<List<Product>, DataError> {
        return if (authRepository.isLoggedIn()) {
            safeApiCall {
                api.toggleWishlist(product.id).items.map { it.toProduct() }
            }
        } else {
            if (localWishlistStorage.isProductWishlisted(product.id)) {
                val items = localWishlistStorage.removeProduct(product.id)
                _localWishlist.value = items
                Result.Success(items)
            } else {
                localWishlistStorage.addProduct(product)
                val items = localWishlistStorage.getWishlistItems()
                _localWishlist.value = items
                Result.Success(items)
            }
        }
    }

    override fun isProductWishlisted(productId: String): Boolean {
        return if (authRepository.isLoggedIn()) {
            _localWishlist.value.any { it.id == productId }
        } else {
            localWishlistStorage.isProductWishlisted(productId)
        }
    }

    override fun observeWishlist(): Flow<List<Product>> {
        return _localWishlist.asStateFlow()
    }
}
