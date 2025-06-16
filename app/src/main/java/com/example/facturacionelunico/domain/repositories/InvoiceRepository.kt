package com.example.facturacionelunico.domain.repositories

import androidx.paging.PagingData
import com.example.facturacionelunico.data.database.entities.DetalleVentaEntity
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDetailDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    fun getInvoices(): Flow<PagingData<InvoiceDomainModel>>

    fun getInvoicesWithDebt(): Flow<PagingData<InvoiceDomainModel>>

    suspend fun createInvoice(
        invoice: InvoiceDomainModel,
        details: List<DetailInvoiceDomainModel>,
        moneyPaid: Double): String

    suspend fun createInvoiceDetail(
        invoiceId: Long,
        products: List<ProductItem>
    ): String

    fun getInvoiceDetailById(id: Long): Flow<ResultPattern<InvoiceDetailDomainModel>>

    suspend fun payInvoice(invoiceId: Long, amount: Double): String

    suspend fun updateInvoiceDetail(newDetail: DetalleVentaEntity, newTotal:Double): String

    suspend fun deleteInvoiceDetail(invoiceDetailId: DetalleVentaEntity, newTotal:Double): String
}