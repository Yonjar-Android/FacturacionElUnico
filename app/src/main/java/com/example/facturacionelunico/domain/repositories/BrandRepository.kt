package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.BrandDomainModel
import kotlinx.coroutines.flow.Flow

interface BrandRepository {
    fun getBrands(): Flow<List<BrandDomainModel>>

    suspend fun createBrand(brandName: String)
}