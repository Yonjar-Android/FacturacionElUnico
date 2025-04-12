package com.example.facturacionelunico.domain.models

data class DetailedProductModel(
    val id: Long,
    val name: String,
    val category: String,
    val brand: String,
    val salePrice: Double,
    val purchasePrice: Double,
    val stock: Int,
    val description: String,
    val photo: String?
)