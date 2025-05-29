package com.example.facturacionelunico.domain.models.purchase

data class PurchaseDetailDomainModel(
    val purchaseId: Long = 0,
    val productId: Long,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)
