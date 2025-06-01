package com.example.facturacionelunico.domain.models.supplier

data class DetailedSupplierLocalModel(
    val id: Long,
    val company: String,
    val contactName: String,
    val phone: String?,
    val email: String?,
    val address: String?,
    val deptTotal: Double
)
