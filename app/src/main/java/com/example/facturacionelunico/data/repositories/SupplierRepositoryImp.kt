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
    // Función para obtener todos los proveedores mediante un flow
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

    override fun getSupplierById(id: Long): Flow<SupplierDomainModel?> {
        return supplierDao.getSupplierById(id).map {
            SupplierDomainModel(
                id = it.id,
                company = it.nombreEmpresa,
                contactName = it.nombreContacto,
                phone = it.telefono,
                email = it.correo,
                address = it.direccion)
        }
    }

    override suspend fun getSuppliersBySearch(query: String): Flow<ResultPattern<List<SupplierDomainModel>>> {
        return supplierDao.getSuppliersBySearch(query)
            .map<List<SupplierDomainModel>, ResultPattern<List<SupplierDomainModel>>> { products ->
                ResultPattern.Success(products)
            }
            .catch { e ->
                emit(ResultPattern.Error(exception = e, message = "Error: ${e.message}"))
            }
    }

    // Función para crear un proveedor
    override suspend fun createSupplier(supplier: SupplierDomainModel): String {
        return runCatching {

            val existingSupplier = supplierDao.getSupplierByCompany(supplier.company, supplier.id)

            if (existingSupplier != null){
                return "Error: Ya existe un proveedor con ese nombre de empresa"
            } else{
                val newSupplier = ProveedorEntity(
                    nombreEmpresa = supplier.company,
                    nombreContacto = supplier.contactName,
                    telefono = supplier.phone ?: "",
                    correo = supplier.email ?: "",
                    direccion = supplier.address ?: ""
                )
                supplierDao.insert(newSupplier)
                "Se ha creado un nuevo proveedor"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }

    override suspend fun updateSupplier(supplier: SupplierDomainModel): String {
        return runCatching {

            val existingSupplier = supplierDao.getSupplierByCompany(supplier.company, supplier.id)

            if (existingSupplier != null){
                return "Error: Ya existe un proveedor con ese nombre de empresa"
            } else{
                val newSupplier = ProveedorEntity(
                    id = supplier.id,
                    nombreEmpresa = supplier.company,
                    nombreContacto = supplier.contactName,
                    telefono = supplier.phone ?: "",
                    correo = supplier.email ?: "",
                    direccion = supplier.address ?: ""
                )
                println(newSupplier)
                supplierDao.update(newSupplier)
                "Se ha actualizado el proveedor"
            }
        }.getOrElse {
            "Error: ${it.message}"
        }
    }
}