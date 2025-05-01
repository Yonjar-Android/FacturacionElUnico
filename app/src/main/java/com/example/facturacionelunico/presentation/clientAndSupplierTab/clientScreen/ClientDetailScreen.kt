package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom

@Composable
fun ClientDetailScreen(
    navController: NavController
){

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBarCustom(
                title = "Cliente",
                onNavigationClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                GenericBlueUiButton(
                    buttonText = "Editar Cliente",
                    onFunction = { showDialog = true }
                )
            }
        }
    ) { paddingValues ->
        paddingValues.calculateTopPadding()
    }
}