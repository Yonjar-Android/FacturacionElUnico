package com.example.facturacionelunico.domain.models.reports

data class ReporteMensualRaw(
    val mes: String,         // "01", "02", ...
    val anio: String,
    val totalVendido: Double?,
    val gananciaNeta: Double?
)