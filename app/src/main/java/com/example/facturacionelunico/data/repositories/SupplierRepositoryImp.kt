package com.example.facturacionelunico.data.repositories

import com.example.facturacionelunico.data.database.dao.ProveedorDao
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import com.example.facturacionelunico.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SupplierRepositoryImp @Inject constructor(
    private val supplierDao: ProveedorDao
): SupplierRepository {
    override fun getSuppliers(): Flow<ResultPattern<List<SupplierDomainModel>>> {
        return supplierDao.getAll()
            .map {
                it.map {
                    SupplierDomainModel(
                        id = it.id,
                        company = it.nombreEmpresa,
                        contactName = it.nombreContacto)
            }
    }.map<List<SupplierDomainModel>, ResultPattern<List<SupplierDomainModel>>> { suppliers ->
        ResultPattern.Success(suppliers)
    }.catch { e ->
        emit(ResultPattern.Error(exception = e, message = e.message))
    }
            }

    override suspend fun createSupplier(supplier: SupplierDomainModel): String {
        return runCatching {
            val newSupplier = ProveedorEntity(
                nombreEmpresa = supplier.company,
                nombreContacto = supplier.contactName,
                telefono = supplier.phone ?: "",
                correo = supplier.email ?: "",
                direccion = supplier.address ?: ""
            )
            supplierDao.insert(newSupplier)
            "Se ha creado un nuevo cliente"
        }.getOrElse {
            "Error: ${it.message}"
        }
    }
}