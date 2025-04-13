package com.example.facturacionelunico.presentation.sharedComponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun TopAppBarCustom(
    title: String,                     // Título dinámico (único parámetro obligatorio)
    onNavigationClick: () -> Unit = {} // Acción de navegación (opcional, vacío por defecto)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigationClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Icono fijo
                contentDescription = "arrow back icon"
            )
        }
        Spacer(modifier = Modifier.weight(1.2f))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(2f))
    }
}