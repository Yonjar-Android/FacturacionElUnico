package com.example.facturacionelunico.domain.models.supplier

data class SupplierDomainModel(
    val id: Long = 0,
    val company: String,
    val contactName: String,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null
)