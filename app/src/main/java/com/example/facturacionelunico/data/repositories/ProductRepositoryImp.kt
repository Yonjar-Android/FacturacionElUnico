package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.database.entities.ProductoEntity
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(
    private val productDao: ProductoDao
) : ProductRepository {
    override fun getProducts(): Flow<List<ProductDomainModel>> {
        return productDao.getAll().map {
            it.map{
                ProductDomainModel(
                    id = it.id,
                    name = it.nombre,
                    idBrand = it.idMarca,
                    stock = it.stock,
                    priceSell = it.precioVenta,
                    priceBuy = it.precioCompra,
                    idCategory = it.idCategoria,
                    description = it.descripcion,
                    photo = it.foto
                )
            }
        }
    }

    override suspend fun createProduct(productDomainModel: ProductDomainModel) {

        val product = ProductoEntity(
            nombre = productDomainModel.name,
            stock = productDomainModel.stock,
            precioVenta = productDomainModel.priceSell,
            precioCompra = productDomainModel.priceBuy,
            idMarca = productDomainModel.idBrand,
            idCategoria = productDomainModel.idCategory,
            descripcion = productDomainModel.description,
            foto = productDomainModel.photo
        )

        productDao.insert(product)

    }
}