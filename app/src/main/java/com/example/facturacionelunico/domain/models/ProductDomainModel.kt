package com.example.facturacionelunico.domain.models

data class ProductDomainModel(
    val id: Long = 0,
    val name: String,
    val idBrand: Long,
    val idCategory: Long,
    val description: String,
    val photo: String?,
    val stock: Int,
    val priceSell: Double,
    val priceBuy: Double
)
