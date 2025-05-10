package com.example.facturacionelunico.domain.models.invoice

data class InvoiceDetailLocalModel(
    val idFactura: Long,
    val nombreCliente: String?,
    val apellidoCliente: String?,
    val totalFactura: Double,
    val totalPendiente: Double
)

