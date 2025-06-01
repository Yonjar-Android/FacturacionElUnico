package com.example.facturacionelunico.domain.models.purchase

import com.example.facturacionelunico.domain.models.ProductItem

data class DetailPurchaseDomainModelUI(
    val id: Long = 0,
    val supplier: String,
    val debt: Double,
    val total: Double,
    val products: List<ProductItem>
)
