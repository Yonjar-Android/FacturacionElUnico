package com.example.facturacionelunico.presentation.databaseScreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun DatabaseScreen(
    viewModel: DatabaseViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val exportStatus by viewModel.exportStatus.collectAsStateWithLifecycle()
    val importStatus by viewModel.importStatus.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.importFromUri(context, it)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            viewModel.exportDatabase()
        }) {
            Text(text = "Exportar datos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            launcher.launch(arrayOf("application/json")) // seleccionar solo archivos .json
        }) {
            Text(text = "Importar desde archivo externo")
        }

    }

    exportStatus?.let { success ->
        Toast.makeText(
            context,
            if (success) "Exportación exitosa" else "Error al exportar",
            Toast.LENGTH_SHORT
        ).show()
        // Reiniciar el estado después de mostrar el toast
        LaunchedEffect(Unit) {
            viewModel.resetExportStatus()
        }
    }

    importStatus?.let { success ->
        Toast.makeText(
            context,
            if (success) "Importación exitosa" else "Error al importar",
            Toast.LENGTH_SHORT
        ).show()
        LaunchedEffect(Unit) {
            viewModel.resetImportStatus()
        }
    }
}