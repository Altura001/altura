package com.example.ultra.catalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.catalog.domain.usecase.GetProductsUseCase
import com.example.ultra.catalog.domain.usecase.GetVendorsUseCase
import com.example.ultra.catalog.presentation.intent.HomeAction
import com.example.ultra.catalog.presentation.intent.HomeEvent
import com.example.ultra.catalog.presentation.intent.HomeState
import com.example.ultra.core.domain.model.Vendor
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

class HomeViewModel(
	private val getVendorsUseCase: GetVendorsUseCase,
	private val getProductsUseCase: GetProductsUseCase,
	private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

	private val _state = MutableStateFlow(HomeState())
	val state: StateFlow<HomeState> = _state.asStateFlow()

	private val _events = Channel<HomeEvent>(Channel.BUFFERED)
	val events = _events.receiveAsFlow()

	init {
		onAction(HomeAction.LoadVendors)
		onAction(HomeAction.LoadAllProducts)
	}

	fun onAction(action: HomeAction) {
		println("kolade $action")
		when (action) {
			is HomeAction.LoadVendors -> loadVendors()
			is HomeAction.LoadAllProducts -> loadAllProducts()
			is HomeAction.SelectVendor -> selectVendor(action.vendorId)
			is HomeAction.ClearSelection -> clearSelection()
			is HomeAction.ClearError -> _state.update { it.copy(error = null) }
			is HomeAction.AddToCart -> addToCart(action.product)
			else -> {}
		}
	}

	private fun loadVendors() {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true, error = null) }
			getVendorsUseCase()
				.onSuccess { vendors: List<Vendor> ->
					println("kolade loadVendors onSuccess $vendors")
					_state.update {
						it.copy(
							isLoading = false,
							vendors = vendors
						)
					}
				}
				.onFailure { error ->
					_state.update { it.copy(isLoading = false, error = error.toUiText()) }
					_events.send(HomeEvent.ShowError(error.toUiText()))
				}
		}
	}

	private fun loadAllProducts() {
		println("loadAllProducts")
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true, error = null) }
			println("loadAllProducts true")

			getProductsUseCase()
				.onSuccess { products ->

					println("kolade products onSuccess $products")

					_state.update {
						it.copy(
							isLoading = false,
							products = products
						)
					}
				}
				.onFailure { error ->
					_state.update { it.copy(isLoading = false, error = error.toUiText()) }
					_events.send(HomeEvent.ShowError(error.toUiText()))
				}
		}
	}

	private fun selectVendor(vendorId: String) {
		val vendor = _state.value.vendors.find { it.id == vendorId }
		_state.update { it.copy(selectedVendor = vendor) }
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true, error = null) }
			getProductsUseCase(vendorId)
				.onSuccess { products ->
					_state.update {
						it.copy(
							isLoading = false,
							products = products
						)
					}
				}
				.onFailure { error ->
					_state.update { it.copy(isLoading = false, error = error.toUiText()) }
					_events.send(HomeEvent.ShowError(error.toUiText()))
				}
		}
	}

	private fun clearSelection() {
		_state.update { it.copy(selectedVendor = null) }
		loadAllProducts()
	}

	private fun addToCart(product: com.example.ultra.core.domain.model.Product) {
		viewModelScope.launch {
			addToCartUseCase(product)
				.onSuccess {
					_events.send(
						HomeEvent.ShowError(
							com.example.ultra.core.presentation.UiText.DynamicString(
								"Added to cart"
							)
						)
					)
				}
				.onFailure { error -> _events.send(HomeEvent.ShowError(error.toUiText())) }
		}
	}
}
