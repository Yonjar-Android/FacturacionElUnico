package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import com.example.facturacionelunico.domain.models.supplier.DetailedSupplierDomainModel
import com.example.facturacionelunico.domain.models.supplier.DetailedSupplierLocalModel
import com.example.facturacionelunico.domain.models.supplier.SupplierDomainModel
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {

    fun getSuppliers(): Flow<ResultPattern<PagingData<DetailedSupplierLocalModel>>>

    suspend fun getSupplierById(id: Long): Flow<DetailedSupplierDomainModel?>

    fun getPurchasesBySupplierId(supplierId:Long): Flow<PagingData<PurchaseDomainModel>>

    suspend fun getSuppliersBySearch(query: String): Flow<ResultPattern<PagingData<DetailedSupplierLocalModel>>>

    suspend fun createSupplier(supplier: SupplierDomainModel): String

    suspend fun updateSupplier(supplier: SupplierDomainModel): String

}