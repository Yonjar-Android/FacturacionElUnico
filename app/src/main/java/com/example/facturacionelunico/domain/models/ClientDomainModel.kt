package com.example.facturacionelunico.domain.models

data class ClientDomainModel(
    val id: Long = 0,
    val name: String,
    val lastName: String,
    val phone: String?,
    val numberIdentifier: Int
)
