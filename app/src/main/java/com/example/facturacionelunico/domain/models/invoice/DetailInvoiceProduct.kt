package com.example.facturacionelunico.domain.models.invoice

data class DetailInvoiceProduct(
    val id: Long,
    val name: String,
    val price: Double,
    val quantity: Int,
    val dateUpdate:Long
)
