package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import kotlinx.coroutines.flow.Flow

interface BrandRepository {
    fun getBrands(): Flow<List<BrandDomainModel>>

    suspend fun getProductsByBrand(brandId: Long): List<DetailedProductModel>

    suspend fun createBrand(brandName: String)

    suspend fun updateBrand(brand: BrandDomainModel)
}