package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getCategories(): Flow<ResultPattern<PagingData<CategoryDomainModel>>>

    fun getCategoryById(categoryId:Long): Flow<CategoryDomainModel>

    fun getCategoryByName(query: String): Flow<ResultPattern<PagingData<CategoryDomainModel>>>

    suspend fun getProductsByCategory(categoryId: Long): List<DetailedProductModel>

    suspend fun createCategory(categoryName: String): String

    suspend fun updateCategory(category: CategoryDomainModel): String

}