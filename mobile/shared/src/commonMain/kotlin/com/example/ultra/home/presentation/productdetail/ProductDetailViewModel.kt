package com.example.ultra.home.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.cart.domain.usecase.UpdateCartItemUseCase
import com.example.ultra.cart.domain.usecase.RemoveFromCartUseCase
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.repository.WishlistRepository
import com.example.ultra.home.domain.usecase.GetProductsUseCase
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.core.presentation.toUiText
import com.example.ultra.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    private var currentCartItemId: String? = null

    init {
        viewModelScope.launch {
            cartRepository.observeCart().collect { cart ->
                val product = _state.value.product
                if (product != null) {
                    val cartItem = cart.items.find { it.productId == product.id }
                    if (cartItem != null) {
                        currentCartItemId = cartItem.id
                        _state.update { it.copy(cartQuantity = cartItem.quantity) }
                    } else {
                        currentCartItemId = null
                        _state.update { it.copy(cartQuantity = 0) }
                    }
                }
            }
        }
    }

    fun onAction(action: ProductDetailAction) {
        when (action) {
            is ProductDetailAction.LoadProduct -> loadProduct(action.handle)
            is ProductDetailAction.AddToCart -> addToCart(action.product)
            is ProductDetailAction.IncrementQuantity -> incrementQuantity()
            is ProductDetailAction.DecrementQuantity -> decrementQuantity(action.product)
            is ProductDetailAction.ToggleWishlist -> toggleWishlist(action.product)
        }
    }

    private fun loadProduct(handle: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProductsUseCase()
                .onSuccess { products ->
                    val product = products.find { it.handle == handle || it.id == handle }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            isWishlisted = product?.let { p -> wishlistRepository.isProductWishlisted(p.id) } ?: false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                    notificationManager.error(error.toUiText().asString())
                }
        }
    }

    private fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(product)
                .onSuccess { items ->
                    val isNowWishlisted = items.any { it.id == product.id }
                    _state.update { it.copy(isWishlisted = isNowWishlisted) }
                    notificationManager.success(
                        if (isNowWishlisted) "Added to wishlist" else "Removed from wishlist"
                    )
                }
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            addToCartUseCase(product)
                .onSuccess { notificationManager.success("Added to cart") }
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }

    private fun incrementQuantity() {
        val itemId = currentCartItemId ?: return
        val currentQty = _state.value.cartQuantity
        viewModelScope.launch {
            updateCartItemUseCase(itemId, currentQty + 1)
                .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
        }
    }

    private fun decrementQuantity(product: Product) {
        val currentQty = _state.value.cartQuantity
        if (currentQty <= 1) {
            val itemId = currentCartItemId ?: return
            viewModelScope.launch {
                removeFromCartUseCase(itemId)
                    .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
            }
        } else {
            val itemId = currentCartItemId ?: return
            viewModelScope.launch {
                updateCartItemUseCase(itemId, currentQty - 1)
                    .onFailure { error -> notificationManager.error(error.toUiText().asString()) }
            }
        }
    }
}
