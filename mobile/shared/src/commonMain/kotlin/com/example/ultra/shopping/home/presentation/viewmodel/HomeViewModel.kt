package com.example.ultra.shopping.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.shopping.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.shopping.home.domain.usecase.GetProductsUseCase
import com.example.ultra.shopping.home.domain.usecase.GetVendorsUseCase
import com.example.ultra.shopping.home.domain.usecase.SearchProductsUseCase
import com.example.ultra.shopping.home.presentation.intent.HomeAction
import com.example.ultra.shopping.home.presentation.intent.HomeState
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.core.presentation.toUiText
import com.example.ultra.core.presentation.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
	private val getVendorsUseCase: GetVendorsUseCase,
	private val getProductsUseCase: GetProductsUseCase,
	private val searchProductsUseCase: SearchProductsUseCase,
	private val addToCartUseCase: AddToCartUseCase,
	private val notificationManager: NotificationManager
) : ViewModel() {

	private val _state = MutableStateFlow(HomeState())
	val state: StateFlow<HomeState> = _state.asStateFlow()

	private var searchJob: Job? = null

	init {
		onAction(HomeAction.LoadVendors)
		onAction(HomeAction.LoadAllProducts)
	}

	fun onAction(action: HomeAction) {
		when (action) {
			is HomeAction.LoadVendors -> loadVendors()
			is HomeAction.LoadAllProducts -> loadAllProducts()
			is HomeAction.SelectVendor -> selectVendor(action.vendorId)
			is HomeAction.ClearSelection -> clearSelection()
			is HomeAction.ClearError -> _state.update { it.copy(error = null) }
			is HomeAction.AddToCart -> addToCart(action.product)
			is HomeAction.Search -> search(action.query)
			is HomeAction.GoBack -> {}
		}
	}

	private fun loadVendors() {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true, error = null) }
			getVendorsUseCase()
				.onSuccess { vendors: List<Vendor> ->
					_state.update {
						it.copy(
							isLoading = false,
							vendors = vendors
						)
					}
				}
				.onFailure { error ->
					_state.update { it.copy(isLoading = false, error = error.toUiText()) }
					notificationManager.error(error.toUiText().asString())
				}
		}
	}

	private fun loadAllProducts() {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true, error = null) }
			getProductsUseCase()
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
					notificationManager.error(error.toUiText().asString())
				}
		}
	}

	private fun search(query: String) {
		_state.update { it.copy(searchQuery = query) }

		searchJob?.cancel()

		if (query.isBlank()) {
			_state.update { it.copy(isSearching = false, products = _state.value.products) }
			loadAllProducts()
			return
		}

		searchJob = viewModelScope.launch {
			_state.update { it.copy(isSearching = true) }
			delay(500)
			searchProductsUseCase(query)
				.onSuccess { products ->
					_state.update {
						it.copy(
							isSearching = false,
							products = products
						)
					}
				}
				.onFailure { error ->
					_state.update { it.copy(isSearching = false) }
					notificationManager.error(error.toUiText().asString())
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
					notificationManager.error(error.toUiText().asString())
				}
		}
	}

	private fun clearSelection() {
		_state.update { it.copy(selectedVendor = null, searchQuery = "") }
		loadAllProducts()
	}

	private fun addToCart(product: com.example.ultra.core.domain.model.Product) {
		viewModelScope.launch {
			addToCartUseCase(product)
				.onSuccess { notificationManager.success("Added to cart") }
				.onFailure { error -> notificationManager.error(error.toUiText().asString()) }
		}
	}
}
