package com.example.ultra.catalog.domain.usecase

import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.domain.repository.CatalogRepository

class GetVendorsUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(): List<Vendor> {
        return repository.getVendors()
    }
}

class GetProductsUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(vendorId: String? = null): List<Product> {
        return if (vendorId != null) {
            repository.getProductsByVendor(vendorId)
        } else {
            repository.getAllProducts()
        }
    }
}
