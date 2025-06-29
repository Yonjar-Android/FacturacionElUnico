@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.productScreenTab.categoryScreen.categoryDetailScreen

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
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.ProductItem
import com.example.facturacionelunico.presentation.sharedComponents.DialogFormCreateUpdate
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom
import com.example.facturacionelunico.utils.transform.FormatNames

@Composable
fun CategoryDetailScreen(
    categoryId: Long,
    navController: NavController,
    categoryDetailScreenViewModel: CategoryDetailScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        categoryDetailScreenViewModel.getProductsByCategory(categoryId)
        categoryDetailScreenViewModel.observeCategory(categoryId)
    }

    val context = LocalContext.current

    val category by categoryDetailScreenViewModel.category.collectAsStateWithLifecycle()

    val products by categoryDetailScreenViewModel.products.collectAsStateWithLifecycle()

    val message by categoryDetailScreenViewModel.message.collectAsStateWithLifecycle()

    var textValue by remember { mutableStateOf(category?.categoryName ?: "") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(category) {
        textValue = category?.categoryName ?: ""
    }

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            categoryDetailScreenViewModel.restartMessage()

            if (!message!!.contains("Error")){
                textValue = ""
                showDialog = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBarCustom(
                title = category?.categoryName ?: "",
                onNavigationClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                GenericBlueUiButton(
                    buttonText = "Editar Categoría",
                    onFunction = { showDialog = true }
                )
            }
        }
    ) { paddingValues ->
        if (products.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
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
            title = "Editar Categoría",
            textButton = "Actualizar",
            value = textValue,
            onValueChange = { textValue = it },
            dismiss = { showDialog = false },
            onConfirm = { name ->
                categoryDetailScreenViewModel.updateCategory(
                    CategoryDomainModel(
                        categoryId = categoryId,
                        categoryName = FormatNames.firstLetterUpperCase(name)
                    )
                )
            },
        )
    }

}
