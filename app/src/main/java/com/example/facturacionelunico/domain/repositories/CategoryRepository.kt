package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getCategories(): Flow<ResultPattern<List<CategoryDomainModel>>>

    fun getCategoryById(categoryId:Long): Flow<CategoryDomainModel>

    fun getCategoryByName(query: String): Flow<ResultPattern<List<CategoryDomainModel>>>

    suspend fun getProductsByCategory(categoryId: Long): List<DetailedProductModel>

    suspend fun createCategory(categoryName: String): String

    suspend fun updateCategory(category: CategoryDomainModel): String

}