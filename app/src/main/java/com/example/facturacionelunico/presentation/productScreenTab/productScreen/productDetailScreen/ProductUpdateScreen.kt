package com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.TextFieldComponent
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.TextFieldDescription
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.TextFieldDrawer
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton

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

    val categories by productDetailViewModel.categories.collectAsStateWithLifecycle()
    val searchQueryCat by productDetailViewModel.searchQueryCategory.collectAsStateWithLifecycle()

    val brands by productDetailViewModel.brands.collectAsStateWithLifecycle()
    val searchQueryBrand by productDetailViewModel.searchQueryBrand.collectAsStateWithLifecycle()

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
            brandId = p.id
            categoryId = p.id
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
                productDetailViewModel.updateProduct(
                    ProductDomainModel(
                        id = product?.id!!,
                        name = productName,
                        idBrand = brandId,
                        idCategory = categoryId,
                        description = description,
                        priceSell = priceSell.toDouble(),
                        priceBuy = priceBuy.toDouble(),
                        stock = stock.toInt(),
                        photo = ""
                    )
                )
            })

        // Show modal to select a category
        if (showDialogCat) {
            ModalSelectionDialogCar(
                query = searchQueryCat,
                title = "Seleccionar Categoría",
                items = categories,
                onDismiss = { showDialogCat = false },
                onItemSelected = {
                    categoryId = it.categoryId
                    category = it.categoryName
                    showDialogCat = false
                },
                viewModel = productDetailViewModel
            )
        }

        // Show modal to select a brand

        if (showDialogBrand) {
            ModalSelectionDialogBrand(
                query = searchQueryBrand,
                title = "Seleccionar Marca",
                items = brands,
                onDismiss = { showDialogBrand = false },
                onItemSelected = {
                    brand = it.brandName
                    brandId = it.brandId
                    showDialogBrand = false
                },
                viewModel = productDetailViewModel
            )
        }

        BackHandler { navController.navigateUp() }
    }
}

@Composable
fun ModalSelectionDialogCar(
    query: String,
    title: String,
    items: List<CategoryDomainModel>,
    onDismiss: () -> Unit,
    onItemSelected: (CategoryDomainModel) -> Unit,
    viewModel: ProductDetailViewModel
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Barra de búsqueda
                TextField(
                    value = query,
                    onValueChange = { newQuery ->
                        viewModel.updateQueryCategory(newQuery) },
                    placeholder = { Text("Buscar...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.updateQueryCategory("")
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de items
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemSelected(item) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.categoryName)
                            Button(onClick = { onItemSelected(item) }) {
                                Text("Elegir")
                            }
                        }

                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ModalSelectionDialogBrand(
    query: String,
    title: String,
    items: List<BrandDomainModel>,
    onDismiss: () -> Unit,
    onItemSelected: (BrandDomainModel) -> Unit,
    viewModel: ProductDetailViewModel
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Barra de búsqueda
                TextField(
                    value = query,
                    onValueChange = { newQuery ->
                        viewModel.updateQueryBrand(newQuery) },
                    placeholder = { Text("Buscar...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.updateQueryBrand("")
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de items
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemSelected(item) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.brandName)
                            Button(onClick = { onItemSelected(item) }) {
                                Text("Elegir")
                            }
                        }

                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}