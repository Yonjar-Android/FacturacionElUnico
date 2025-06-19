package com.example.facturacionelunico.presentation.reportScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportScreen(
    viewModel: ReporteViewModel = hiltViewModel()
){
    val reporte by viewModel.reporteMensual.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargarReporteDelMes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reporte de Ventas del Mes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (reporte != null) {
            Text("Total vendido: C$ ${reporte!!.totalVendido ?: 0.0}")
            Text("Ganancia neta: C$ ${reporte!!.gananciaNeta ?: 0.0}")
        } else {
            CircularProgressIndicator()
        }
    }
}