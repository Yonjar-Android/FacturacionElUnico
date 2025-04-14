package com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.TextFieldComponent
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.TextFieldDescription
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.TextFieldDrawer
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom

@Composable
fun ProductUpdateScreen(
    productId: Long,
    navController: NavController,
    productDetailViewModel: ProductDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        productDetailViewModel.loadProduct(productId)
    }

    val product by productDetailViewModel.product.collectAsStateWithLifecycle()

    var productName by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var priceSell by remember { mutableStateOf("") }
    var priceBuy by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var category by remember { mutableStateOf("") }
    var categoryId by remember { mutableLongStateOf(0) }

    var brand by remember { mutableStateOf("") }
    var brandId by remember { mutableLongStateOf(0) }

    var showDialogCat by remember { mutableStateOf(false) }
    var showDialogBrand by remember { mutableStateOf(false) }

    LaunchedEffect(product) {
        product?.let { p ->
            productName = p.name
            stock = p.stock.toString()
            priceSell = p.salePrice.toString()
            priceBuy = p.purchasePrice.toString()
            description = p.description
            category = p.category
            brand = p.brand
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "arrow back icon"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Editar Producto", fontSize = 24.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(2f))
        }

        Spacer(modifier = Modifier.size(5.dp))

        TextFieldComponent(
            textFieldName = "Nombre del Producto",
            textValue = productName,
            onTextValueChange = { productName = it }
        )

        TextFieldDrawer(
            textFieldName = "Categoría",
            textValue = category,
            onTextValueChange = {},
            showSelection = { showDialogCat = true }
        )

        TextFieldDrawer(
            textFieldName = "Marca",
            textValue = brand,
            onTextValueChange = {},
            showSelection = { showDialogBrand = true }
        )

        TextFieldDescription(
            textFieldName = "Descripción",
            textValue = description,
            onTextValueChange = { description = it }
        )

        TextFieldComponent(
            textFieldName = "Stock",
            textValue = stock.toString(),
            onTextValueChange = { stock = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextFieldComponent(
            textFieldName = "Precio Venta",
            textValue = priceSell.toString(),
            onTextValueChange = { priceSell = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        TextFieldComponent(
            textFieldName = "Precio Compra",
            textValue = priceBuy.toString(),
            onTextValueChange = { priceBuy = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        GenericBlueUiButton(buttonText = "Actualizar",
            onFunction = {

            })

        BackHandler { navController.navigateUp() }
    }
}