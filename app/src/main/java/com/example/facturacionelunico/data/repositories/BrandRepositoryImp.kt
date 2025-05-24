package com.example.facturacionelunico.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
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
    override fun getBrands(): Flow<ResultPattern<PagingData<BrandDomainModel>>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5),
            pagingSourceFactory = { brandDao.getAll() }
        ).flow
            .map { pagingData ->
                pagingData.map { entity ->
                    BrandDomainModel(
                        brandId = entity.id,
                        brandName = entity.nombre
                    )
                }
            }.map { domainPagingData ->
                ResultPattern.Success(domainPagingData)
            }.catch { e ->
                ResultPattern.Error(exception = e, message = "Error: ${e.message}")
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

    override fun getBrandByName(query: String): Flow<ResultPattern<PagingData<BrandDomainModel>>> {
        return return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5),
            pagingSourceFactory = { brandDao.getBrandByName(query) }
        ).flow
            .map { pagingData ->
                pagingData.map { entity ->
                    BrandDomainModel(
                        brandId = entity.id,
                        brandName = entity.nombre
                    )
                }
            }.map { domainPagingData ->
                ResultPattern.Success(domainPagingData)
            }.catch { e ->
                ResultPattern.Error(exception = e, message = "Error: ${e.message}")
            }
    }

    override suspend fun getProductsByBrand(brandId: Long): List<DetailedProductModel> {
        return brandDao.getDetailedByBrandId(brandId)
    }

    override suspend fun createBrand(brandName: String): String {
        return runCatching {

            val brandExist = brandDao.existBrandName(brandName)
            if (brandExist != null) {
                return "Error: La marca ya existe"
            }else{
                brandDao.insert(
                    MarcaEntity(
                        nombre = brandName
                    )
                )
                "Marca creada exitosamente"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }

    }

    override suspend fun updateBrand(brand: BrandDomainModel): String {
        return runCatching {

            val conflictingBrand = brandDao.getOtherBrandByName(brand.brandName, brand.brandId)
            if (conflictingBrand != null){
                "Error: Ya existe otra marca con ese nombre"
            } else{
                brandDao.update(
                    MarcaEntity(
                        id = brand.brandId,
                        nombre = brand.brandName
                    )
                )
                "Marca actualizada exitosamente"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }
}