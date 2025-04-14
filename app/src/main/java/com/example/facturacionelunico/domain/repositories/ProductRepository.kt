package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<DetailedProductModel>>

    suspend fun getProductById(productId: Long): DetailedProductModel?

    suspend fun getProductBySearch(query: String): Flow<List<DetailedProductModel>>

    suspend fun createProduct(productDomainModel: ProductDomainModel)

    suspend fun updateProduct(productDomainModel: ProductDomainModel)
}