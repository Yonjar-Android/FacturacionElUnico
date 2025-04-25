package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.MarcaDao
import com.example.facturacionelunico.data.database.entities.MarcaEntity
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.repositories.BrandRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BrandRepositoryImp @Inject constructor(
    private val brandDao: MarcaDao
) : BrandRepository {
    override fun getBrands(): Flow<ResultPattern<List<BrandDomainModel>>> {
        return brandDao.getAll().map {
            it.map {
                BrandDomainModel(
                    brandId = it.id,
                    brandName = it.nombre
                )
            }
        }.map<List<BrandDomainModel>, ResultPattern<List<BrandDomainModel>>> { brands ->
            ResultPattern.Success(brands)
        }.catch { e ->
            emit(ResultPattern.Error(exception = e, message = "Error: ${e.message}"))
        }

    }

    override fun getBrandById(brandId: Long): Flow<BrandDomainModel> {
        return brandDao.getBrandById(brandId).map {
            BrandDomainModel(
                brandId = it.id,
                brandName = it.nombre
            )
        }
    }

    override fun getBrandByName(query: String): Flow<ResultPattern<List<BrandDomainModel>>> {
        return brandDao.getBrandByName(query)
            .map {
                it.map {
                    BrandDomainModel(
                        brandId = it.id,
                        brandName = it.nombre
                    )
                }
            }.map<List<BrandDomainModel>, ResultPattern<List<BrandDomainModel>>> { brands ->
                ResultPattern.Success(brands)
            }.catch { e ->
                emit(ResultPattern.Error(exception = e, message = "Error: ${e.message}"))
            }
    }

    override suspend fun getProductsByBrand(brandId: Long): List<DetailedProductModel> {
        return brandDao.getDetailedByBrandId(brandId)
    }

    override suspend fun createBrand(brandName: String): String {
        return runCatching {
            brandDao.insert(
                MarcaEntity(
                    nombre = brandName
                )
            )
            "Marca creada exitosamente"
        }.getOrElse {
            "Error: ${it.message}"
        }

    }

    override suspend fun updateBrand(brand: BrandDomainModel): String {
        return runCatching {
            brandDao.update(
                MarcaEntity(
                    id = brand.brandId,
                    nombre = brand.brandName
                )
            )
            "Marca actualizada exitosamente"
        }.getOrElse {
            "Error: ${it.message}"
        }

    }
}