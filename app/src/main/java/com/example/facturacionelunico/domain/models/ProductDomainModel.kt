package com.example.facturacionelunico.domain.models

data class ProductDomainModel(
    val productId: String,
    val productName: String,
    val brand: String,
    val price: Float,
    val stock: Int
)
