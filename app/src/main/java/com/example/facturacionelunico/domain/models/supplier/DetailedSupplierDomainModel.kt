package com.example.facturacionelunico.domain.models.supplier

import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel

data class DetailedSupplierDomainModel(
    val id: Long,
    val company: String,
    val contactName: String,
    val phone: String,
    val email: String,
    val address: String,
    val debt: Double,
    val purchases: List<PurchaseDomainModel> = emptyList()
)
