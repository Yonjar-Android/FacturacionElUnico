package com.example.facturacionelunico.domain.repositories

import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel

interface InvoiceRepository {

    suspend fun getInvoices()

    suspend fun createInvoice(
        invoice: InvoiceDomainModel,
        details: List<DetailInvoiceDomainModel>,
        moneyPaid: Double): String


}