package com.example.facturacionelunico.presentation.reportScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facturacionelunico.data.database.AppDatabase
import com.example.facturacionelunico.domain.models.ReporteMensualDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val db: AppDatabase
): ViewModel() {

    private val _reporteMensual = MutableStateFlow<ReporteMensualDto?>(null)
    val reporteMensual: StateFlow<ReporteMensualDto?> = _reporteMensual

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarReporteDelMes(actual: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val mes = actual.monthValue.toString().padStart(2, '0')  // "06"
            val anio = actual.year.toString()                        // "2025"
            val reporte = db.detalleVentaDao().getReporteMensual(mes, anio)
            _reporteMensual.value = reporte

            println("Reporte mensual: $reporte")
        }
    }
}
