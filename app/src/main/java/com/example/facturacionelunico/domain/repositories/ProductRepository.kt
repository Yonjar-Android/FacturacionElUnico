package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.ProductDomainModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<ProductDomainModel>>

    suspend fun createProduct(productDomainModel: ProductDomainModel)
}