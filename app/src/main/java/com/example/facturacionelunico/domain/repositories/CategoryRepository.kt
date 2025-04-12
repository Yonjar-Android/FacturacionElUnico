package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.CategoryDomainModel
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getCategories(): Flow<List<CategoryDomainModel>>

    suspend fun createCategory(categoryName: String)

}