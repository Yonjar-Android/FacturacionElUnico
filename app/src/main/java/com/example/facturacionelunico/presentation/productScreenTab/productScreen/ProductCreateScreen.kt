package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.utils.transform.FormatNames

@Composable
fun ProductCreateScreen(
    navController: NavController,
    context: Context,
    viewModel: ProductCreateScreenViewModel = hiltViewModel()
) {

    val categories: LazyPagingItems<CategoryDomainModel> = viewModel.categories.collectAsLazyPagingItems()
    val searchQueryCat by viewModel.searchQueryCategory.collectAsStateWithLifecycle()

    val brands: LazyPagingItems<BrandDomainModel> = viewModel.brands.collectAsLazyPagingItems()
    val searchQueryBrand by viewModel.searchQueryBrand.collectAsStateWithLifecycle()

    val message by viewModel.message.collectAsState()
    val navigateBack by viewModel.back.collectAsStateWithLifecycle()

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

    // Muestra el mensaje ya sea error o éxito al cambiar desde el viewModel
    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.restartMessage()

            if (navigateBack) {
                navController.navigateUp()
            }
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
                "Registrar Nuevo Producto", fontSize = 24.sp,
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
            onTextValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*$"))) {
                    stock = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextFieldComponent(
            textFieldName = "Precio Venta",
            textValue = priceSell.toString(),
            onTextValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    priceSell = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        TextFieldComponent(
            textFieldName = "Precio Compra",
            textValue = priceBuy.toString(),
            onTextValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    priceBuy = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        GenericBlueUiButton(
            buttonText = "Guardar",
            onFunction = {
                viewModel.createProduct(
                    ProductDomainModel(
                        name = FormatNames.firstLetterUpperCase(productName),
                        priceBuy = priceBuy.toDoubleOrNull() ?: 0.0,
                        priceSell = priceSell.toDoubleOrNull() ?: 0.0,
                        description = description,
                        stock = stock.toIntOrNull() ?: 0,
                        idCategory = categoryId,
                        idBrand = brandId,
                        photo = ""
                    )
                )
            }
        )

    }

    // Show modal to select a category
    if (showDialogCat) {
        SelectionDialog(
            query = searchQueryCat,
            title = "Seleccionar Categoría",
            items = categories,
            onDismiss = { showDialogCat = false },
            onItemSelected = {
                categoryId = it.categoryId
                category = it.categoryName
                showDialogCat = false
            },
            updateQueryCat = { viewModel.updateQueryCategory(it) }
        )
    }

    // Show modal to select a brand

    if (showDialogBrand) {
        SelectionDialogBrand(
            query = searchQueryBrand,
            title = "Seleccionar Marca",
            items = brands,
            onDismiss = { showDialogBrand = false },
            onItemSelected = {
                brand = it.brandName
                brandId = it.brandId
                showDialogBrand = false
            },
            updateQueryBrand = { viewModel.updateQueryBrand(it) }
        )
    }

}

@Composable
fun TextFieldComponent(
    textFieldName: String,
    textValue: String,
    onTextValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(fraction = 0.85f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(textFieldName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.size(5.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(30.dp)
                ),
            value = textValue, onValueChange = { onTextValueChange(it) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(30.dp),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = keyboardOptions
        )
    }

    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun TextFieldDrawer(
    textFieldName: String,
    textValue: String,
    showSelection: () -> Unit,
    onTextValueChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título del campo (igual que antes)
        Row(
            modifier = Modifier.fillMaxWidth(fraction = 0.85f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(textFieldName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.size(5.dp))

        // Nuevo diseño que reemplaza al TextField
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .height(56.dp) // Altura similar a TextField
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(30.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(30.dp)
                )
                .clickable { showSelection() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (textValue.isEmpty()) "Seleccionar" else textValue,
                    color = if (textValue.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Desplegar opciones",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun TextFieldDescription(
    textFieldName: String,
    textValue: String,
    onTextValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(fraction = 0.85f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(textFieldName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.size(5.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .height(100.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(30.dp)
                ),
            value = textValue, onValueChange = { onTextValueChange(it) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(30.dp),
            maxLines = 3,
        )
    }
}

@Composable
fun SelectionDialog(
    query: String,
    title: String,
    items: LazyPagingItems<CategoryDomainModel>,
    onDismiss: () -> Unit,
    onItemSelected: (CategoryDomainModel) -> Unit,
    updateQueryCat: (String) -> Unit
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
                        updateQueryCat.invoke(newQuery)
                    },
                    placeholder = { Text("Buscar...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                updateQueryCat.invoke("")
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de items
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(
                        count = items.itemCount,
                        key = { items[it]?.categoryId ?: 0 },
                        contentType = items.itemContentType{"Categories"}
                    ) { index ->
                        val item = items[index]
                        if (item != null){
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
fun SelectionDialogBrand(
    query: String,
    title: String,
    items: LazyPagingItems<BrandDomainModel>,
    onDismiss: () -> Unit,
    onItemSelected: (BrandDomainModel) -> Unit,
    updateQueryBrand: (String) -> Unit
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
                        updateQueryBrand.invoke(newQuery)
                    },
                    placeholder = { Text("Buscar...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                updateQueryBrand.invoke("")
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de items
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(
                        count = items.itemCount,
                        key = { items[it]?.brandId ?: 0 },
                        contentType = items.itemContentType{"Brands"}
                    ) { index ->

                        val item = items[index]

                        if(item != null){
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
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}