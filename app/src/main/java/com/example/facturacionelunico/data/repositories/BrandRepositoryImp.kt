package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.MarcaDao
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.repositories.BrandRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BrandRepositoryImp @Inject constructor(
    private val brandDao: MarcaDao
) : BrandRepository {
    override fun getBrands(): Flow<List<BrandDomainModel>> {
        return brandDao.getAll().map {
            it.map {
                BrandDomainModel(
                    brandId = it.id,
                    brandName = it.nombre
                )
            }
        }

    }

    override suspend fun createBrand(brandName: String) {
        brandDao.insert(
            MarcaEntity(
                nombre = brandName
            )
        )
    }
}