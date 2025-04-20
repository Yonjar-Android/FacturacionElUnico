package com.example.facturacionelunico.domain.models

data class DetailedProductModel(
    val id: Long,
    val name: String,
    val category: String,
    val categoryId: Long?,
    val brand: String,
    val brandId: Long?,  // Nuevo campo: ID de la marca (puede ser null)
    val salePrice: Double,
    val purchasePrice: Double,
    val stock: Int,
    val description: String,
    val photo: String?
)