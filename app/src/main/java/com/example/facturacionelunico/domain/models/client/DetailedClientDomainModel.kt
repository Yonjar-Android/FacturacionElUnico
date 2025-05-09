package com.example.facturacionelunico.domain.models.client

import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel

data class DetailedClientDomainModel(
    val id: Long,
    val name: String,
    val lastName: String,
    val phone: String,
    val numberIdentifier: Int,
    val deptTotal: Double,
    val invoices: List<InvoiceDomainModel> = emptyList()
)