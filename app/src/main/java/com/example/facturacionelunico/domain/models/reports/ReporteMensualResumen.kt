package com.example.facturacionelunico.domain.models.reports

data class ReporteMensualResumen(
    val mes: String,         // "Enero 2025"
    val totalVendido: Double,
    val gananciaNeta: Double
)