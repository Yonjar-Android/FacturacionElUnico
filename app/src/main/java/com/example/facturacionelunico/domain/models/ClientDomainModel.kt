package com.example.facturacionelunico.domain.models

data class ClientDomainModel(
    val id: Long,
    val name: String,
    val lastName: String,
    val phone: String?,
)
