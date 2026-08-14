package com.example.ultra.catalog.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.catalog.domain.usecase.GetProductsUseCase
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    private val _events = Channel<ProductDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: ProductDetailAction) {
        when (action) {
            is ProductDetailAction.LoadProduct -> loadProduct(action.handle)
            is ProductDetailAction.AddToCart -> addToCart(action.product)
        }
    }

    private fun loadProduct(handle: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProductsUseCase()
                .onSuccess { products ->
                    val product = products.find { it.handle == handle || it.id == handle }
                    _state.update { it.copy(isLoading = false, product = product) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                    _events.send(ProductDetailEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun addToCart(product: Product) {
        viewModelScope.launch {
            addToCartUseCase(product)
                .onSuccess { _state.update { it.copy(addedToCart = true) } }
                .onFailure { error -> _events.send(ProductDetailEvent.ShowError(error.toUiText())) }
        }
    }
}
