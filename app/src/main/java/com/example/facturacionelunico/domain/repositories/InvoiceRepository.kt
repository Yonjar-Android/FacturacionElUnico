package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.ResultPattern
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDetailDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    suspend fun getInvoices(): List<InvoiceDomainModel>

    suspend fun createInvoice(
        invoice: InvoiceDomainModel,
        details: List<DetailInvoiceDomainModel>,
        moneyPaid: Double): String

    fun getInvoiceDetailById(id: Long): Flow<ResultPattern<InvoiceDetailDomainModel>>

    suspend fun payInvoice(invoiceId: Long, amount: Double): String
}