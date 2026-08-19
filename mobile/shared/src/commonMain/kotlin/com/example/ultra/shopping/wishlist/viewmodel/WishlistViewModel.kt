package com.example.ultra.shopping.wishlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.shopping.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.core.domain.repository.WishlistRepository
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.core.presentation.toUiText
import com.example.ultra.core.presentation.toUiText
import com.example.ultra.shopping.wishlist.presentation.intent.WishlistAction
import com.example.ultra.shopping.wishlist.presentation.intent.WishlistState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _state = MutableStateFlow(WishlistState())
    val state: StateFlow<WishlistState> = _state.asStateFlow()

    init {
        onAction(WishlistAction.LoadWishlist)
        observeWishlist()
    }

    fun onAction(action: WishlistAction) {
        when (action) {
            is WishlistAction.LoadWishlist -> loadWishlist()
            is WishlistAction.RemoveFromWishlist -> removeFromWishlist(action.productId)
            is WishlistAction.AddToCart -> addToCart(action.product)
            is WishlistAction.GoBack -> {}
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            wishlistRepository.observeWishlist().collect { items ->
                _state.update { it.copy(items = items) }
            }
        }
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            wishlistRepository.getWishlist()
                .onSuccess { items ->
                    _state.update { it.copy(isLoading = false, items = items) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                    notificationManager.error(error.toUiText().asString())
                }
        }
    }

    private fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(productId)
                .onSuccess { notificationManager.success("Removed from wishlist") }
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }

    private fun addToCart(product: com.example.ultra.core.domain.model.Product) {
        viewModelScope.launch {
            addToCartUseCase(product)
                .onSuccess {
                    removeFromWishlist(product.id)
                    notificationManager.success("Added to cart")
                }
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }
}
