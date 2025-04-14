package com.example.facturacionelunico.presentation.sharedComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopAppBarCustom(
    title: String,                     // Título dinámico (único parámetro obligatorio)
    onNavigationClick: () -> Unit = {} // Acción de navegación (opcional, vacío por defecto)
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        IconButton(onClick = onNavigationClick,
            modifier = Modifier.align(Alignment.TopStart)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Icono fijo
                contentDescription = "arrow back icon"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .height(38.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

}