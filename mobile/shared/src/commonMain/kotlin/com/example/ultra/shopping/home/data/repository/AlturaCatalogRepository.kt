package com.example.ultra.shopping.home.data.repository

import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.data.toPickupStation
import com.example.ultra.core.data.toProduct
import com.example.ultra.core.data.toVendor
import com.example.ultra.core.data.util.safeApiCall
import com.example.ultra.core.domain.model.PickupStation
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.model.Vendor
import com.example.ultra.core.domain.repository.CatalogRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.core.domain.util.map

/** CatalogRepository backed by the Altura Nova .NET backend. */
class AlturaCatalogRepository(
    private val api: AlturaApiService
) : CatalogRepository {

    override suspend fun getVendors(): Result<List<Vendor>, DataError.Network> =
        safeApiCall { api.getVendors() }.map { list -> list.map { it.toVendor() } }

    override suspend fun getAllProducts(): Result<List<Product>, DataError.Network> =
        safeApiCall { api.getProducts(pageSize = 100) }.map { page -> page.items.map { it.toProduct() } }

    override suspend fun getProductsByVendor(vendorId: String): Result<List<Product>, DataError.Network> =
        safeApiCall { api.getVendorProducts(vendorId) }.map { page -> page.items.map { it.toProduct() } }

    override suspend fun searchProducts(query: String): Result<List<Product>, DataError.Network> =
        safeApiCall { api.getProducts(search = query, pageSize = 50) }.map { page -> page.items.map { it.toProduct() } }

    override suspend fun getProductById(productId: String): Result<Product, DataError.Network> =
        safeApiCall { api.getProductById(productId).toProduct() }

    override suspend fun getProductByHandle(handle: String): Result<Product?, DataError.Network> =
        safeApiCall { api.getProductByHandle(handle).toProduct() }

    override suspend fun getPickupStations(): Result<List<PickupStation>, DataError.Network> =
        safeApiCall { api.getPickupStations() }.map { list -> list.map { it.toPickupStation() } }
}
