package com.example.facturacionelunico.presentation.reportScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.data.database.AppDatabase
import com.example.facturacionelunico.domain.models.reports.ReporteMensualDto
import com.example.facturacionelunico.domain.models.reports.ReporteMensualResumen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {

    private val _reporteMensual = MutableStateFlow<ReporteMensualDto?>(null)
    val reporteMensual: StateFlow<ReporteMensualDto?> = _reporteMensual

    private val _reporteAnual = MutableStateFlow<List<ReporteMensualResumen>?>(null)
    val reporteAnual: StateFlow<List<ReporteMensualResumen>?> = _reporteAnual

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarReporteDelMes(actual: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val mes = actual.monthValue.toString().padStart(2, '0')  // "06"
            val anio = actual.year.toString()                        // "2025"
            val reporte = db.detalleVentaDao().getReporteMensual(mes, anio)
            _reporteMensual.value = reporte
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarResumenAnual(anio: Int = LocalDate.now().year) {
        viewModelScope.launch {
            val rawList = db.detalleVentaDao().getResumenPorAnio(anio.toString())

            val formateado = rawList.map {
                val nombreMes = obtenerNombreMes(it.mes.toInt())
                ReporteMensualResumen(
                    mes = nombreMes,
                    totalVendido = it.totalVendido ?: 0.0,
                    gananciaNeta = it.gananciaNeta ?: 0.0
                )
            }

            _reporteAnual.value = formateado
        }
    }

    fun obtenerNombreMes(numeroMes: Int): String {
        return when (numeroMes) {
            1 -> "Ene"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Abr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Ago"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dic"
            else -> "Mes inválido"
        }
    }
}
