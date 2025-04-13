@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.productScreenTab.brandScreen.brandDetailScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.ProductItem
import com.example.facturacionelunico.presentation.sharedComponents.DialogFormCreateUpdate
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom

@Composable
fun BrandDetailScreen(
    brandId: Long,
    brandName:String,
    navController: NavController,
    brandDetailScreenViewModel: BrandDetailScreenViewModel = hiltViewModel()
){
    LaunchedEffect(Unit) {
        brandDetailScreenViewModel.getProductsByBrand(brandId)
    }

    val products by brandDetailScreenViewModel.products.collectAsStateWithLifecycle()

    var textValue by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Barra superior con título y flecha para navegar hacia atrás
        TopAppBarCustom(
            title = brandName,
            onNavigationClick = { navController.navigateUp() }
        )

        if (products.isNotEmpty()){
            LazyColumn {
                items(products) {
                    ProductItem(
                        it,
                        controller = navController
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        GenericBlueUiButton(
            buttonText = "Editar Marca",
            onFunction = {
                showDialog = true
            }
        )

        Spacer(modifier = Modifier.size(10.dp))
    }

    if (showDialog){
        DialogFormCreateUpdate(
            title = "Editar Marca",
            textButton = "Actualizar",
            value = textValue,
            onValueChange = { textValue = it },
            dismiss = { showDialog = false },
            onConfirm = {name ->
                brandDetailScreenViewModel.updateBrand(
                    BrandDomainModel(
                        brandId = brandId,
                        brandName = textValue
                    )
                )
            }
        )
    }

}

