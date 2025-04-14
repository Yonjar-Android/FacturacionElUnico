package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.CategoriaDao
import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImp @Inject constructor(
    private val categoryDao: CategoriaDao
): CategoryRepository {
    override fun getCategories(): Flow<List<CategoryDomainModel>> {
        return categoryDao.getAll().map {
            it.map {
                CategoryDomainModel(
                    categoryId = it.id,
                    categoryName = it.nombre
                )
            }
        }
    }

    override fun getCategoryById(categoryId: Long): Flow<CategoryDomainModel> {
        return categoryDao.getCategoryById(categoryId)
            .map { CategoryDomainModel(
                categoryId = it.id,
                categoryName = it.nombre
            ) }
    }

    override suspend fun getProductsByCategory(categoryId: Long): List<DetailedProductModel> {
        return categoryDao.getDetailedByCategoryId(categoryId)
    }

    override suspend fun createCategory(categoryName: String) {
        categoryDao.insert(
            CategoriaEntity(
                nombre = categoryName
            )
        )
    }

    override suspend fun updateCategory(category: CategoryDomainModel) {
        categoryDao.update(
            CategoriaEntity(
                id = category.categoryId,
                nombre = category.categoryName
            )
        )
    }
}