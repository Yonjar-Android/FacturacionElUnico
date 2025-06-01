package com.example.facturacionelunico.domain.models.purchase

data class PurchaseDetailLocalModel(
    val id: Long,
    val company: String,
    val total: Double,
    val totalPendiente: Double,
)
