package com.example.facturacionelunico.presentation.reportScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import java.time.LocalDate
import java.time.format.TextStyle


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportScreen(
    viewModel: ReporteViewModel = hiltViewModel()
) {
    val reporte by viewModel.reporteMensual.collectAsStateWithLifecycle()

    val reporteAnual by viewModel.reporteAnual.collectAsStateWithLifecycle()

    val aniosDisponibles by viewModel.aniosDisponibles.collectAsStateWithLifecycle()

    var anioActual by remember { mutableIntStateOf(LocalDate.now().year) }

    val ventasPorMes by remember(reporteAnual) {
        derivedStateOf {
            reporteAnual?.map { it.totalVendido } ?: listOf(0.0)
        }
    }

    val nombresMeses by remember(reporteAnual) {
        derivedStateOf {
            reporteAnual?.map { it.mes } ?: emptyList()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargarReporteDelMes()
        viewModel.cargarResumenAnual()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reporte de Ventas del Mes", style = MaterialTheme.typography.titleLarge)
        Text("Año: ${LocalDate.now().month}", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (reporte != null) {
            Text("Total vendido: C$ ${reporte!!.totalVendido ?: 0.0}")
            Text("Ganancia neta: C$ ${reporte!!.gananciaNeta ?: 0.0}")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Reporte de Ventas Anual", style = MaterialTheme.typography.titleLarge)
            Text("Año: ${anioActual}", style = MaterialTheme.typography.titleLarge)

            SelectorDeAnios(
                anios = aniosDisponibles,
                anioSeleccionado = LocalDate.now().year,
                onAnioSeleccionado = {
                    viewModel.cargarResumenAnual(it)
                    anioActual = it
                }
            )

            val lines = listOf(
                Line(
                    label = "Ventas",
                    values = ventasPorMes,
                    color = SolidColor(Color(0xFF23af92)),
                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(200, easing = EaseIn),
                    gradientAnimationDelay = 200,
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                )
            )

            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = lines,
                animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = nombresMeses,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp, textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp, textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onBackground
                    ), padding = 16.dp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            CircularProgressIndicator()
        }

    }
}

@Composable
fun SelectorDeAnios(
    anios: List<Int>,
    anioSeleccionado: Int?,
    onAnioSeleccionado: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = anioSeleccionado?.toString() ?: "Seleccionar año",
            onValueChange = {},
            readOnly = true,
            label = { Text("Año") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Detectar click manualmente con Box que cubre el TextField
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    expanded = true
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            anios.forEach { anio ->
                DropdownMenuItem(
                    text = { Text(anio.toString()) },
                    onClick = {
                        onAnioSeleccionado(anio)
                        expanded = false
                    }
                )
            }
        }
    }
}
