package com.example.facturacionelunico.domain.models.invoice

data class InvoiceDomainModel(
    val id: Long = 0,
    val sellDate: Long,
    val total: Double,
    val clientId: Long?,
    val state: String,
    val paymentMethod: String
)

data class DetailInvoiceDomainModel(
    val id: Long = 0,
    val invoiceId: Long,
    val productId: Long,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)