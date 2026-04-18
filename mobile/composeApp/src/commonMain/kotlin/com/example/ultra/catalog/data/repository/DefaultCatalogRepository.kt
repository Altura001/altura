package com.example.ultra.catalog.data.repository

import android.util.Log
import com.example.ultra.core.data.MedusaApiService
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.domain.repository.CatalogRepository

class DefaultCatalogRepository(
    private val apiService: MedusaApiService
) : CatalogRepository {
    
    companion object {
        private const val TAG = "CatalogRepo"
    }
    
    private val fakeVendors = listOf(
        Vendor(
            id = "vendor_1",
            name = "Medusa Store",
            description = "Official Medusa products"
        )
    )
    
    override suspend fun getVendors(): List<Vendor> {
        return try {
            Log.d(TAG, "getVendors called")
            fakeVendors
        } catch (e: Exception) {
            Log.e(TAG, "getVendors error: ${e.message}")
            fakeVendors
        }
    }
    
    override suspend fun getAllProducts(): List<Product> {
        return try {
            Log.d(TAG, "getAllProducts called")
            val products = apiService.getProducts()
            Log.d(TAG, "Got ${products.size} products from API")
            products
        } catch (e: Exception) {
            Log.e(TAG, "getAllProducts error: ${e.message}")
            emptyList()
        }
    }
    
    override suspend fun getProductsByVendor(vendorId: String): List<Product> {
        return try {
            apiService.getProducts()
        } catch (e: Exception) {
            Log.e(TAG, "getProductsByVendor error: ${e.message}")
            emptyList()
        }
    }
    
    override suspend fun getProductById(productId: String): Product {
        return apiService.getProducts().first { it.id == productId }
    }
    
    override suspend fun getProductByHandle(handle: String): Product? {
        return try {
            apiService.getProductByHandle(handle)
        } catch (e: Exception) {
            null
        }
    }
}