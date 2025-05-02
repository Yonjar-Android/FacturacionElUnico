package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {

    fun getSuppliers(): Flow<ResultPattern<List<SupplierDomainModel>>>

    suspend fun createSupplier(supplier: SupplierDomainModel): String

}