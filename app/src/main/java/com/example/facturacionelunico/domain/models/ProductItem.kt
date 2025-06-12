package com.example.facturacionelunico.domain.models

data class ProductItem(
    val detailId: Long = 0L,
    val id: Long = 0L,
    val name: String,
    val price: Double,
    val purchasePrice: Double,
    val quantity: Int
) {
    val subtotal: Double
        get() = price * quantity
}