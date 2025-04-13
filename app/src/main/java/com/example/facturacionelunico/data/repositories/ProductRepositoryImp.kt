package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ProductoDao
import com.example.facturacionelunico.data.mappers.ProductMapper
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(
    private val productDao: ProductoDao
) : ProductRepository {
    override fun getProducts(): Flow<List<DetailedProductModel>> {
        return productDao.getAllDetailed()
    }

    override suspend fun getProductById(productId: Long): DetailedProductModel? {
        return productDao.getDetailedById(productId)
    }

    override suspend fun createProduct(productDomainModel: ProductDomainModel) {
        productDao.insert(ProductMapper.toEntity(productDomainModel))
    }

    override suspend fun updateProduct(productDomainModel: ProductDomainModel) {
        productDao.update(ProductMapper.toEntity(productDomainModel))
    }
}