@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.productScreenTab.brandScreen.brandDetailScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.ProductItem
import com.example.facturacionelunico.presentation.sharedComponents.DialogFormCreateUpdate
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom
import com.example.facturacionelunico.utils.transform.FormatNames

@Composable
fun BrandDetailScreen(
    brandId: Long,
    navController: NavController,
    brandDetailScreenViewModel: BrandDetailScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(brandId) {
        brandDetailScreenViewModel.getProductsByBrand(brandId)
        brandDetailScreenViewModel.observeBrand(brandId)
    }

    val context = LocalContext.current

    val brand by brandDetailScreenViewModel.brand.collectAsStateWithLifecycle()

    val products by brandDetailScreenViewModel.products.collectAsStateWithLifecycle()

    val message by brandDetailScreenViewModel.message.collectAsStateWithLifecycle()

    var textValue by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(brand) {
        textValue = brand?.brandName ?: ""
    }

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            brandDetailScreenViewModel.restartMessage()

            if (!message!!.contains("Error")){
                textValue = ""
                showDialog = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBarCustom(
                title = brand?.brandName ?: "Marca",
                onNavigationClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                GenericBlueUiButton(
                    buttonText = "Editar Marca",
                    onFunction = { showDialog = true }
                )
            }
        }
    ) { paddingValues ->
        if (products.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(products) {
                    ProductItem(
                        it,
                        controller = navController
                    )
                }
            }
        }
    }

    if (showDialog) {
        DialogFormCreateUpdate(
            title = "Editar Marca",
            textButton = "Actualizar",
            value = textValue,
            onValueChange = { textValue = it },
            dismiss = { showDialog = false },
            onConfirm = { name ->
                brandDetailScreenViewModel.updateBrand(
                    BrandDomainModel(
                        brandId = brandId,
                        brandName = FormatNames.firstLetterUpperCase(name),
                    )
                )
            }
        )
    }
}

