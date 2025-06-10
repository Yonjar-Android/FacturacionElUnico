package com.example.facturacionelunico.domain.models

data class ProductItem(
    val id: Long = 0L,
    val name: String,
    val price: Double,
    val purchasePrice: Double,
    val quantity: Int
) {
    val subtotal: Double
        get() = price * quantity
}