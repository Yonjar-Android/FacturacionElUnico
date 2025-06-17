package com.example.facturacionelunico.presentation.sellScreen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.client.DetailedClientLocalModel
import com.example.facturacionelunico.domain.models.invoice.DetailInvoiceDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.utils.validations.ValidationFunctions

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SellScreen(
    navController: NavController,
    viewModel: SellScreenViewModel = hiltViewModel()
) {

    val message by viewModel.message.collectAsStateWithLifecycle()

    val products: LazyPagingItems<DetailedProductModel> =
        viewModel.products.collectAsLazyPagingItems()

    val clients: LazyPagingItems<DetailedClientLocalModel> =
        viewModel.clients.collectAsLazyPagingItems()

    val searchQueryProduct by viewModel.searchQueryProduct.collectAsStateWithLifecycle()
    val searchQueryClient by viewModel.searchQueryClient.collectAsStateWithLifecycle()

    var context = LocalContext.current

    var showClients by remember { mutableStateOf(false) }
    var showProducts by remember { mutableStateOf(false) }
    var showEdiDeleteDialog by remember { mutableStateOf(false) }

    var enabledRadioButtons by remember { mutableStateOf(true) }
    var validationClient by remember { mutableStateOf(true) }

    val dept = listOf<String>("Débito", "Crédito")
    var deptSelectedOption by remember { mutableStateOf(dept[0]) }

    var productList by remember {
        mutableStateOf(
            mutableListOf<ProductItem>()
        )
    }

    var productId by remember { mutableStateOf("") }
    var product by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableDoubleStateOf(0.0) }
    var quantity by remember { mutableStateOf("") }
    var client by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf<Long?>(null) }
    var total by remember { mutableDoubleStateOf(0.0) }
    var moneyToPay by remember { mutableStateOf("") }

    var quantityToModify by remember { mutableIntStateOf(0) }
    var productToModify by remember { mutableLongStateOf(0) }

    if (!message.isNullOrEmpty()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if (message?.contains("Error") == false) {
            productList = mutableListOf()
            validationClient = true
            enabledRadioButtons = true
            client = ""
            moneyToPay = ""

        }
        viewModel.restartMessage()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    GenericBlueUiButton(
                        buttonText = "Guardar",
                        onFunction = {
                            if (productList.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "No ha agregado productos en la factura",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@GenericBlueUiButton
                            }
                            if (deptSelectedOption == "Crédito") {
                                if (!ValidationFunctions.isValidDouble(moneyToPay)) {
                                    Toast.makeText(
                                        context,
                                        "Rellene el campo de dinero a pagar con un valor numérico válido",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@GenericBlueUiButton
                                }
                                if (moneyToPay.toDouble() < 0) {
                                    Toast.makeText(
                                        context,
                                        "Rellene el campo de dinero a pagar con un valor positivo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@GenericBlueUiButton
                                }

                                if(moneyToPay.toDouble() > total) {
                                    Toast.makeText(
                                        context,
                                        "El dinero a pagar no puede ser mayor al total de la compra",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@GenericBlueUiButton
                                }
                            }

                            viewModel.createInvoice(
                                invoice = InvoiceDomainModel(
                                    sellDate = System.currentTimeMillis(),
                                    total = total,
                                    clientId = clientId,
                                    state = if (dept[0] == deptSelectedOption) "COMPLETADO" else "PENDIENTE",
                                    paymentMethod = if (dept[0] == deptSelectedOption) "DEBITO" else "CREDITO"
                                ),
                                details = productList.map {
                                    DetailInvoiceDomainModel(
                                        invoiceId = 0L,
                                        productId = it.id,
                                        quantity = it.quantity,
                                        price = it.price,
                                        subtotal = it.subtotal,
                                        purchasePrice = it.purchasePrice
                                    )
                                },
                                moneyPaid = if (dept[0] == deptSelectedOption) total else moneyToPay.toDouble()
                            )
                        }
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.size(10.dp))

                Text("Factura", fontSize = 32.sp, fontWeight = FontWeight.Bold)

                val clientList = listOf<String>("Con cliente", "Sin Cliente")
                var selectedOption by remember { mutableStateOf(clientList[0]) }

                /*Radio button para seleccionar si se va a facturar con cliente o sin cliente*/
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    clientList.forEach { client ->
                        Row(
                            Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption == client,
                                onClick = {
                                    selectedOption = client
                                    if (client == "Sin Cliente") {
                                        deptSelectedOption = dept[0]
                                    }
                                },
                                enabled = enabledRadioButtons
                            )
                            Text(text = client, color = Color.Black)
                        }
                    }
                }

                if (selectedOption == clientList[0]) {
                    ClickableTextField(
                        title = "Cliente",
                        value = client,
                        onClick = { showClients = true },
                        enabled = enabledRadioButtons
                    )
                    validationClient = true
                } else {
                    client = ""
                    validationClient = false
                    clientId = null
                }

                ClickableTextField(
                    title = "Producto",
                    value = product,
                    onClick = { showProducts = true }
                )

                TextFieldInvoice(
                    title = "Precio",
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            price = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextFieldInvoice(
                    title = "Cantidad",
                    value = quantity,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*$"))) {
                            quantity = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                GenericBlueUiButton(
                    buttonText = "Agregar",
                    onFunction = {
                        // Validamos los campos para posteriormente agregarlos a la tabla
                        if (!validations(
                                enabledRadioButtonsClient = validationClient,
                                client = client,
                                product = product,
                                price = price,
                                quantity = quantity,
                                context = context
                            )
                        ) {
                            return@GenericBlueUiButton
                        }

                        if (productList.any {
                            it.id == productId.toLong()
                        }) {

                            Toast.makeText(
                                context,
                                "El producto ya se encuentra en la tabla",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@GenericBlueUiButton
                        }

                        productList = productList.toMutableList().apply {
                            add(
                                ProductItem(
                                    id = productId.toLong(),
                                    name = product,
                                    price = price.toDouble(),
                                    quantity = quantity.toInt(),
                                    purchasePrice = purchasePrice.toDouble()
                                )
                            )
                        }

                        enabledRadioButtons = false

                        // Limpiar campos luego de agregar a la tabla
                        productId = ""
                        product = ""
                        price = ""
                        quantity = ""
                        purchasePrice = 0.0

                        // Suma del total
                        total = productList.sumOf { it.subtotal }
                    }
                )

                // Tabla de productos a facturar
                InvoiceTable(
                    productList = productList,
                    showDialog = { quantity, id ->
                        showEdiDeleteDialog = true
                        quantityToModify = quantity
                        productToModify = id
                    }
                )


                // Radiobuttons para seleccionar si el pago es con débito o crédito

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dept.forEach { dept ->
                        Row(
                            Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = deptSelectedOption == dept,
                                onClick = { deptSelectedOption = dept },
                                enabled = if (selectedOption == clientList[1]) false else true
                            )
                            Text(text = dept, color = Color.Black)
                        }
                    }
                }

                if (deptSelectedOption == "Crédito") {
                    TextFieldInvoice(
                        title = "Pagar",
                        value = moneyToPay,
                        onValueChange = { moneyToPay = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }


            }
        }
        if (showClients) {
            SelectClientTable(
                clients = clients,
                closeTable = { showClients = false },
                getValues = { name, id ->
                    client = name
                    clientId = id
                    showClients = false
                },
                searchProduct = { queryClient ->
                    viewModel.updateQueryClient(queryClient)
                },
                searchQueryClient = searchQueryClient
            )
        }


        if (showProducts) {
            SelectProductTable(
                products = products,
                closeTable = { showProducts = false },
                getValues = { name, id, precio, precioCompra ->
                    productId = id.toString()
                    product = name
                    price = precio.toString()
                    purchasePrice = precioCompra
                    showProducts = false
                },
                searchQuery = { query ->
                    viewModel.updateQueryProduct(query)
                },
                searchQueryProduct = searchQueryProduct
            )
        }
    }



    if (showEdiDeleteDialog) {
        ProductOptionsDialog(
            context = context,
            currentQuantity = quantityToModify,
            onEditClick = {
                showEdiDeleteDialog = false

                productList.toMutableList().apply {
                    val index = indexOfFirst { it.id == productToModify }
                    if (index != -1) {
                        val updatedItem = this[index].copy(quantity = it)
                        this[index] = updatedItem
                        productList = this // Esto actualiza el estado y dispara recomposición
                    }
                }

                quantityToModify = 0
            },
            onDismiss = {
                showEdiDeleteDialog = false
            },
            onDeleteClick = {
                productList.toMutableList().apply {
                    removeIf { it.id == productToModify }
                    productList = this
                }

                if (productList.isEmpty()) enabledRadioButtons = true

                showEdiDeleteDialog = false
            })
    }
}

@Composable
fun TextFieldInvoice(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier.fillMaxWidth(fraction = 0.9f),
    ) {

        Text(
            title, fontWeight = FontWeight.Bold, fontSize = 16.sp,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.size(5.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = {
                onValueChange.invoke(it)
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color(0xFFdfdfdf),
                unfocusedContainerColor = Color(0xFFdfdfdf)
            ),
            shape = RoundedCornerShape(30.dp),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = keyboardOptions,
        )

        Spacer(modifier = Modifier.size(15.dp))
    }
}

@Composable
fun ClickableTextField(
    title: String,
    value: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.size(5.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFFdfdfdf)) // Color similar al TextField por defecto
                .clickable {
                    if (enabled) onClick()
                }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value.ifEmpty { "Seleccionar..." },
                color = if (value.isEmpty()) Color.Gray else Color.Black
            )
        }

        Spacer(modifier = Modifier.size(15.dp))
    }
}

@Composable
fun InvoiceTable(productList: List<ProductItem>, showDialog: (Int, Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Producto", Modifier.weight(1f), fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Precio", Modifier.weight(1f), fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Cantidad", Modifier.weight(1f), fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Subtotal", Modifier.weight(1f), fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider()

        // Filas de productos
        productList.forEach { product ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDialog(product.quantity, product.id)
                    }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(product.name, Modifier.weight(1f), textAlign = TextAlign.Center)

                Text(
                    "%.2f".format(product.price),
                    Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text("${product.quantity}", Modifier.weight(1f), textAlign = TextAlign.Center)

                Text(
                    "%.2f".format(product.subtotal),
                    Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        // Total
        val total = productList.sumOf { it.subtotal }
        Text(
            "Total: %.2f C$".format(total),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

/**/

@Composable
fun ProductOptionsDialog(
    onDismiss: () -> Unit,
    currentQuantity: Int,
    onEditClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    context: Context
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(currentQuantity) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.8f)
                .fillMaxHeight(fraction = 0.2f)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Opciones del producto", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showEditDialog = true }
                ) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onDeleteClick
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            }
        }
    }

    if (showEditDialog) {
        var quantityText by remember { mutableStateOf(quantity.toString()) }
        Dialog(
            onDismissRequest = {
                showEditDialog = false
                onDismiss.invoke()
            }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.8f)
                    .fillMaxHeight(fraction = 0.25f)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White)
                    .padding(30.dp)
            ) {
                Text("Cantidad actual", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { newValue ->
                        quantityText = newValue
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { showEditDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val newQuantity = quantityText.toIntOrNull()
                            if (newQuantity != null && newQuantity > 0) {
                                onEditClick(newQuantity) // ✅ Solo se llama si el valor es válido
                                showEditDialog = false
                            } else {
                                Toast.makeText(context, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
                            }
                            showEditDialog = false
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }


        }
    }
}

/*Tabla de clientes para seleccionar*/
@Composable
fun SelectClientTable(
    clients: LazyPagingItems<DetailedClientLocalModel>,
    closeTable: () -> Unit,
    getValues: (String, Long) -> Unit,
    searchProduct: (String) -> Unit,
    searchQueryClient: String
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = {
                closeTable.invoke()
            }
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Default.Close, contentDescription = "Close Icon",
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Buscar cliente", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Spacer(modifier = Modifier.size(5.dp))

            SearchBarComponent(
                value = searchQueryClient,
                onChangeValue = {
                    searchProduct.invoke(it)
                }
            )

            Spacer(modifier = Modifier.size(5.dp))

            LazyColumn {
                items(
                    count = clients.itemCount,
                    key = { index -> clients[index]?.id ?: index },
                    contentType = clients.itemContentType { "Clients" }
                ) { index ->

                    val client = clients[index]

                    if (client != null) {
                        ClientItemTable(
                            client,
                            getValues = { name, id ->
                                getValues.invoke(name, id)
                            })
                    }
                }
            }
        }
    }
}

/*Item que se muestra en la tabla de clientes*/

@Composable
fun ClientItemTable(
    client: DetailedClientLocalModel,
    getValues: (String, Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(
                "${client.name} ${client.lastName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text("Número: ${client.numberIdentifier}", fontSize = 16.sp, color = Color.Gray)
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            GenericBlueUiButton(
                buttonText = "Seleccionar",
                onFunction = {
                    getValues.invoke(
                        "${client.name} ${client.lastName}",
                        client.id
                    )
                }
            )
        }
    }
}

/*Tabla de productos para seleccionar*/
@Composable
fun SelectProductTable(
    products: LazyPagingItems<DetailedProductModel>,
    closeTable: () -> Unit,
    getValues: (String, Long, Double, Double) -> Unit,
    searchQuery: (String) -> Unit,
    searchQueryProduct: String
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = {
                closeTable.invoke()
            }
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Default.Close, contentDescription = "Close Icon",
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Buscar producto", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Spacer(modifier = Modifier.size(5.dp))

            SearchBarComponent(
                value = searchQueryProduct,
                onChangeValue = {
                    searchQuery.invoke(it)
                })

            Spacer(modifier = Modifier.size(5.dp))

            LazyColumn {
                items(
                    count = products.itemCount,
                    key = { index -> products[index]?.id ?: index },
                    contentType = products.itemContentType { "Products" }
                ) { index ->

                    val product = products[index]

                    if (product != null) {
                        ProductItemTable(
                            product,
                            getValues = { name, id, precio, precioCompra ->
                                getValues.invoke(name, id, precio, precioCompra)
                            })
                    }
                }
            }
        }
    }
}

/*Item que se muestra en la tabla de productos*/
@Composable
fun ProductItemTable(
    product: DetailedProductModel,
    getValues: (String, Long, Double, Double) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(product.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Text("Precio: ${product.salePrice} C$", fontSize = 16.sp, color = Color.Gray)
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            GenericBlueUiButton(
                buttonText = "Seleccionar",
                onFunction = {
                    getValues.invoke(
                        product.name,
                        product.id,
                        product.salePrice,
                        product.purchasePrice
                    )
                }
            )
        }
    }
}


// Validaciones
fun validations(
    enabledRadioButtonsClient: Boolean,
    client: String,
    product: String,
    price: String,
    quantity: String,
    context: Context
): Boolean {
    if (enabledRadioButtonsClient) {
        if (client.isEmpty()) {
            Toast.makeText(context, "Seleccione un cliente", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    if (product.isEmpty()) {
        Toast.makeText(context, "Seleccione un producto", Toast.LENGTH_SHORT).show()
        return false
    }
    if (price.isEmpty()) {
        Toast.makeText(context, "Rellene el campo precio", Toast.LENGTH_SHORT).show()
        return false
    }

    if (!ValidationFunctions.isValidDouble(price.toString())) {
        Toast.makeText(
            context,
            "Rellene el campo precio con un valor numérico válido",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    if (price.toDouble() <= 0) {
        Toast.makeText(
            context,
            "Rellene el campo precio con un valor mayor que 0",
            Toast.LENGTH_SHORT
        ).show()
    }

    if (quantity.isEmpty()) {
        Toast.makeText(context, "Rellene el campo cantidad", Toast.LENGTH_SHORT).show()
        return false
    }

    if (quantity.toInt() <= 0) {
        Toast.makeText(
            context,
            "Rellene el campo cantidad con un valor entero positivo",
            Toast.LENGTH_SHORT
        ).show()
    }

    if (!ValidationFunctions.isValidInt(quantity.toString())) {
        Toast.makeText(
            context,
            "Rellene el campo cantidad con un valor entero válido",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    return true
}

