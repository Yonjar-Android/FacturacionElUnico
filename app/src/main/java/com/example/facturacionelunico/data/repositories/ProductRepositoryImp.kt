package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.mappers.ProductMapper
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(
    private val productDao: ProductoDao
) : ProductRepository {
    // Función para obtener todos los productos mediante un flow
    override fun getProducts(): Flow<ResultPattern<List<DetailedProductModel>>> {
        return productDao.getAllDetailed()
            .map<List<DetailedProductModel>, ResultPattern<List<DetailedProductModel>>> { products ->
                ResultPattern.Success(products)
            }
            .catch { e ->
                emit(ResultPattern.Error(exception = e, message = e.message))
            }
    }

    // Función para obtener producto por su id
    override suspend fun getProductById(productId: Long): ResultPattern<DetailedProductModel?> {
        return runCatching {
            productDao.getDetailedById(productId)
        }.fold(
            onSuccess = {
                if (it != null) {
                    ResultPattern.Success(it)
                } else {
                    ResultPattern.Error(
                        exception = Throwable("Producto no encontrado"),
                        message = "Error: Producto no encontrado"
                    )
                }
            },
            onFailure = { ResultPattern.Error(it, message = "Error: ${it.message}") }
        )
    }

    //función para obtener producto por búsqueda
    override suspend fun getProductBySearch(query: String): Flow<ResultPattern<List<DetailedProductModel>>> {
        return productDao.getProductsBySearch(query)
            .map<List<DetailedProductModel>, ResultPattern<List<DetailedProductModel>>> { products ->
                ResultPattern.Success(products)
            }
            .catch { e ->
                emit(ResultPattern.Error(exception = e, message = "Error: ${e.message}"))
            }
    }

    // Función crear producto
    override suspend fun createProduct(productDomainModel: ProductDomainModel): String {
        return runCatching {


            val productExist = productDao.existProductName(productDomainModel.name)
            if (productExist != null) {
                "Error: El producto ya existe"
            } else {
                productDao.insert(ProductMapper.toEntity(productDomainModel))
                "Se ha agregado un nuevo producto"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    //Función actualizar producto
    override suspend fun updateProduct(productDomainModel: ProductDomainModel): String {
        return runCatching {

            val productExist =
                productDao.getOtherProductByName(productDomainModel.name, productDomainModel.id)

            if (productExist != null) {
                "Error: Ya existe un producto con ese nombre"
            } else {
                productDao.update(ProductMapper.toEntity(productDomainModel))
                "Se ha actualizado el producto"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }

    }
}