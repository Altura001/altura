package com.example.ultra.catalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.catalog.domain.usecase.GetProductsUseCase
import com.example.ultra.catalog.domain.usecase.GetVendorsUseCase
import com.example.ultra.catalog.presentation.intent.CatalogIntent
import com.example.ultra.catalog.presentation.intent.CatalogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val getVendorsUseCase: GetVendorsUseCase,
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(CatalogState())
    val state: StateFlow<CatalogState> = _state.asStateFlow()
    
    init {
        onAction(CatalogIntent.LoadVendors)
        onAction(CatalogIntent.LoadAllProducts)
    }
    
    fun onAction(action: CatalogIntent) {
        when (action) {
            is CatalogIntent.LoadVendors -> loadVendors()
            is CatalogIntent.LoadAllProducts -> loadAllProducts()
            is CatalogIntent.SelectVendor -> selectVendor(action.vendorId)
            is CatalogIntent.ClearSelection -> clearSelection()
            is CatalogIntent.ClearError -> clearError()
        }
    }
    
    private fun loadVendors() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val vendors = getVendorsUseCase()
                _state.update { it.copy(isLoading = false, vendors = vendors) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun loadAllProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val products = getProductsUseCase()
                _state.update { it.copy(isLoading = false, products = products) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun selectVendor(vendorId: String) {
        val vendor = _state.value.vendors.find { it.id == vendorId }
        _state.update { it.copy(selectedVendor = vendor) }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val products = getProductsUseCase(vendorId)
                _state.update { it.copy(isLoading = false, products = products) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun clearSelection() {
        _state.update { it.copy(selectedVendor = null) }
        loadAllProducts()
    }
    
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
