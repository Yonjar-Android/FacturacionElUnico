package com.example.facturacionelunico.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.facturacionelunico.data.database.dao.CompraDao
import com.example.facturacionelunico.data.database.dao.ProveedorDao
import com.example.facturacionelunico.data.database.entities.ProveedorEntity
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import com.example.facturacionelunico.domain.models.supplier.DetailedSupplierDomainModel
import com.example.facturacionelunico.domain.models.supplier.DetailedSupplierLocalModel
import com.example.facturacionelunico.domain.models.supplier.SupplierDomainModel
import com.example.facturacionelunico.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SupplierRepositoryImp @Inject constructor(
    private val supplierDao: ProveedorDao,
    private val purchaseDao: CompraDao
) : SupplierRepository {
    // Función para obtener todos los proveedores mediante un flow
    override fun getSuppliers(): Flow<ResultPattern<PagingData<DetailedSupplierLocalModel>>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5),
            pagingSourceFactory = { supplierDao.getSuppliersWithDebt() }
        ).flow
            .map { pagingData ->
                pagingData.map {
                    DetailedSupplierLocalModel(
                        id = it.id,
                        company = it.company,
                        contactName = it.contactName,
                        phone = it.phone,
                        email = it.email,
                        address = it.address,
                        deptTotal = it.deptTotal
                    )
                }
            }.map { domainPaginData ->
                ResultPattern.Success(domainPaginData)
            }.catch { e ->
                ResultPattern.Error(exception = e, message = "Error: ${e.message}")
            }
    }

    override suspend fun getSupplierById(id: Long): Flow<DetailedSupplierDomainModel?> {

        return supplierDao.getSupplierById(id).map {
            DetailedSupplierDomainModel(
                id = it.id,
                company = it.company,
                contactName = it.contactName,
                phone = it.phone.toString(),
                email = it.email.toString(),
                address = it.address.toString(),
                debt = it.deptTotal
            )
        }
    }

    override fun getPurchasesBySupplierId(supplierId: Long): Flow<PagingData<PurchaseDomainModel>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5),
            pagingSourceFactory = { purchaseDao.getPurchasesBySupplierId(supplierId) }
        ).flow
            .map {
                it.map {
                    PurchaseDomainModel(
                        purchaseId = it.id,
                        purchaseDate = it.fechaCompra,
                        total = it.total,
                        supplierId = it.idProveedor,
                        state = it.estado
                    )
                }
            }
    }

    override suspend fun getSuppliersBySearch(query: String): Flow<ResultPattern<PagingData<DetailedSupplierLocalModel>>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5),
            pagingSourceFactory = { supplierDao.getSuppliersBySearch(query) }
        ).flow
            .map { pagingData ->
                ResultPattern.Success(pagingData)
            }.catch { e ->
                ResultPattern.Error(exception = e, message = "Error: ${e.message}")
            }
    }

    // Función para crear un proveedor
    override suspend fun createSupplier(supplier: SupplierDomainModel): String {
        return runCatching {

            val existingSupplier = supplierDao.getSupplierByCompany(supplier.company, supplier.id)

            if (existingSupplier != null) {
                return "Error: Ya existe un proveedor con ese nombre de empresa"
            } else {
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

            if (existingSupplier != null) {
                return "Error: Ya existe un proveedor con ese nombre de empresa"
            } else {
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