package com.example.facturacionelunico.domain.models.purchase

data class PurchaseDomainModel(
    val purchaseId: Long = 0,
    val purchaseDate: Long,
    val total: Double,
    val supplierId: Long,
    val state: String
)