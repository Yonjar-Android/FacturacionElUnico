package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.CategoriaDao
import com.example.facturacionelunico.data.database.entities.CategoriaEntity
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImp @Inject constructor(
    private val categoryDao: CategoriaDao
) : CategoryRepository {
    override fun getCategories(): Flow<ResultPattern<List<CategoryDomainModel>>> {
        return categoryDao.getAll().map {
            it.map {
                CategoryDomainModel(
                    categoryId = it.id,
                    categoryName = it.nombre
                )
            }
        }.map<List<CategoryDomainModel>, ResultPattern<List<CategoryDomainModel>>> { categories ->
            ResultPattern.Success(categories)
        }.catch { e ->
                emit(ResultPattern.Error(exception = e, message = e.message))
            }
    }


    override fun getCategoryById(categoryId: Long): Flow<CategoryDomainModel> {
        return categoryDao.getCategoryById(categoryId)
            .map {
                CategoryDomainModel(
                    categoryId = it.id,
                    categoryName = it.nombre
                )
            }
    }

    override fun getCategoryByName(query: String): Flow<ResultPattern<List<CategoryDomainModel>>> {
        return categoryDao.getCategoryByName(query).map {
            it.map {
                CategoryDomainModel(
                    categoryId = it.id,
                    categoryName = it.nombre
                )
            }
        }.map<List<CategoryDomainModel>, ResultPattern<List<CategoryDomainModel>>> { categories ->
            ResultPattern.Success(categories)
        }.catch { e ->
            emit(ResultPattern.Error(exception = e, message = e.message))
        }
    }

    override suspend fun getProductsByCategory(categoryId: Long): List<DetailedProductModel> {
        return categoryDao.getDetailedByCategoryId(categoryId)
    }

    override suspend fun createCategory(categoryName: String): String {
        return runCatching {

            val existCategory = categoryDao.existCategoryName(categoryName)

            if (existCategory != null){
                "Error: La categoría ya existe"
            } else{
                categoryDao.insert(
                    CategoriaEntity(
                        nombre = categoryName
                    )
                )
                "Categoría creada exitosamente"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun updateCategory(category: CategoryDomainModel): String {
        return runCatching {

            val conflictingCategory = categoryDao.getOtherCategoryByName(category.categoryName, category.categoryId)

            if (conflictingCategory != null){
                "Error: Ya existe otra categoría con ese nombre"
            }else{

                categoryDao.update(
                    CategoriaEntity(
                        id = category.categoryId,
                        nombre = category.categoryName
                    )
                )
                "Categoría actualizada exitosamente"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }

    }
}