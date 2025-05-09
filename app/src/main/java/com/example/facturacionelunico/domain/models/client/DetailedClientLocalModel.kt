package com.example.facturacionelunico.domain.models.client

data class DetailedClientLocalModel(
    val id: Long,
    val name: String,
    val lastName: String,
    val phone: String,
    val numberIdentifier: Int,
    val deptTotal: Double,
)