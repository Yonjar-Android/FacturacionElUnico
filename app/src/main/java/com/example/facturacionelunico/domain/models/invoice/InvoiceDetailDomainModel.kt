package com.example.facturacionelunico.domain.models.invoice

import com.example.facturacionelunico.domain.models.ProductItem

data class InvoiceDetailDomainModel(
    val invoiceId: Long,
    val clientName: String,
    val total: Double,
    val debt: Double,
    val products: List<ProductItem>
)
