package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getCategories(): Flow<List<CategoryDomainModel>>

    suspend fun getProductsByCategory(categoryId: Long): List<DetailedProductModel>

    suspend fun createCategory(categoryName: String)

    suspend fun updateCategory(category: CategoryDomainModel)

}