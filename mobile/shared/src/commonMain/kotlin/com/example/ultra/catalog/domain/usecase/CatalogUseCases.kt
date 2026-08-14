package com.example.ultra.catalog.domain.usecase

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.domain.repository.CatalogRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result

class GetVendorsUseCase(private val repository: CatalogRepository) {
	suspend operator fun invoke(): Result<List<Vendor>, DataError.Network> {
		return repository.getVendors()
	}
}

class GetProductsUseCase(private val repository: CatalogRepository) {
	suspend operator fun invoke(vendorId: String? = null): Result<List<Product>, DataError.Network> {
		return if (vendorId != null) {
			repository.getProductsByVendor(vendorId)
		} else {
			repository.getAllProducts()
		}
	}
}
