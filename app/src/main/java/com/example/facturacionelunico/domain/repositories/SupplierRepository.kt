package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {

    fun getSuppliers(): Flow<ResultPattern<PagingData<SupplierDomainModel>>>

    fun getSupplierById(id: Long): Flow<SupplierDomainModel?>

    suspend fun getSuppliersBySearch(query: String): Flow<ResultPattern<PagingData<SupplierDomainModel>>>

    suspend fun createSupplier(supplier: SupplierDomainModel): String

    suspend fun updateSupplier(supplier: SupplierDomainModel): String

}