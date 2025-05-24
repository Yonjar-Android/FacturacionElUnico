package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<ResultPattern<PagingData<DetailedProductModel>>>

    suspend fun getProductById(productId: Long): ResultPattern<DetailedProductModel?>

    suspend fun getProductBySearch(query: String): Flow<ResultPattern<PagingData<DetailedProductModel>>>

    suspend fun createProduct(productDomainModel: ProductDomainModel): String

    suspend fun updateProduct(productDomainModel: ProductDomainModel): String
}