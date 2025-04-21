package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import kotlinx.coroutines.flow.Flow

interface BrandRepository {
    fun getBrands(): Flow<ResultPattern<List<BrandDomainModel>>>

    fun getBrandById(brandId:Long):Flow<BrandDomainModel>

    fun getBrandByName(query: String): Flow<ResultPattern<List<BrandDomainModel>>>

    suspend fun getProductsByBrand(brandId: Long): List<DetailedProductModel>

    suspend fun createBrand(brandName: String):String

    suspend fun updateBrand(brand: BrandDomainModel):String
}