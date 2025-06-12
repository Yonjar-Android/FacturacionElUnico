package com.example.facturacionelunico.presentation.buyScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.domain.models.purchase.PurchaseDetailDomainModel
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import com.example.facturacionelunico.domain.models.supplier.DetailedSupplierLocalModel
import com.example.facturacionelunico.presentation.sellScreen.ClickableTextField
import com.example.facturacionelunico.presentation.sellScreen.InvoiceTable
import com.example.facturacionelunico.presentation.sellScreen.ProductOptionsDialog
import com.example.facturacionelunico.presentation.sellScreen.SelectProductTable
import com.example.facturacionelunico.presentation.sellScreen.TextFieldInvoice
import com.example.facturacionelunico.presentation.sellScreen.validations
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.utils.validations.ValidationFunctions

@Composable
fun BuyScreen(
    navController: NavController,
    viewModel: BuyScreenViewModel = hiltViewModel()
) {
    val message by viewModel.message.collectAsStateWithLifecycle()

    val products: LazyPagingItems<DetailedProductModel> =
        viewModel.products.collectAsLazyPagingItems()

    val suppliers: LazyPagingItems<DetailedSupplierLocalModel> =
        viewModel.suppliers.collectAsLazyPagingItems()

    val searchQueryProduct by viewModel.searchQueryProduct.collectAsStateWithLifecycle()
    val searchQuerySupplier by viewModel.searchQuerySupplier.collectAsStateWithLifecycle()

    var context = LocalContext.current

    var showSuppliers by remember { mutableStateOf(false) }
    var showProducts by remember { mutableStateOf(false) }

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
    var quantity by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var supplierId by remember { mutableStateOf<Long?>(null) }
    var total by remember { mutableDoubleStateOf(0.0) }
    var moneyToPay by remember { mutableStateOf("") }

    var quantityToModify by remember { mutableIntStateOf(0) }
    var productToModify by remember { mutableLongStateOf(0) }
    var showEdiDeleteDialog by remember { mutableStateOf(false) }

    if (!message.isNullOrEmpty()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if (message?.contains("Error") == false) {
            productList = mutableListOf()
            validationClient = true
            enabledRadioButtons = true
            supplier = ""
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
                                    "No ha agregado productos en la compra",
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
                            }

                            viewModel.createPurchase(
                                purchase = PurchaseDomainModel(
                                    purchaseDate = System.currentTimeMillis(),
                                    total = total,
                                    supplierId = supplierId!!,
                                    state = if (dept[0] == deptSelectedOption) "COMPLETADO" else "PENDIENTE"
                                ),
                                details = productList.map {
                                    PurchaseDetailDomainModel(
                                        productId = it.id,
                                        quantity = it.quantity,
                                        price = it.price,
                                        subtotal = it.subtotal
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

                Text("Compra", fontSize = 32.sp, fontWeight = FontWeight.Bold)





                ClickableTextField(
                    title = "Proveedor",
                    value = supplier,
                    onClick = { showSuppliers = true },
                    enabled = enabledRadioButtons
                )
                validationClient = true


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
                                enabledRadioButtonsClient = false,
                                client = supplier,
                                product = product,
                                price = price,
                                quantity = quantity,
                                context = context
                            )
                        ) {
                            return@GenericBlueUiButton
                        }

                        if(productList.any { it.id == productId.toLong() }) {
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
                                    purchasePrice = 0.0,
                                )
                            )
                        }
                        enabledRadioButtons = false

                        // Limpiar campos luego de agregar a la tabla
                        productId = ""
                        product = ""
                        price = ""
                        quantity = ""

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
        if (showSuppliers) {
            SelectSupplierTable(
                suppliers = suppliers,
                closeTable = { showSuppliers = false },
                getValues = { name, id ->
                    supplier = name
                    supplierId = id
                    showSuppliers = false
                },
                searchProduct = { queryClient ->
                    viewModel.updateQuerySupplier(queryClient)
                },
                searchQueryClient = searchQuerySupplier
            )
        }

        if (showProducts) {
            SelectProductTable(
                products = products,
                closeTable = { showProducts = false },
                getValues = { name, id, precio, precioNoUsar ->
                    productId = id.toString()
                    product = name
                    price = precio.toString()
                    showProducts = false
                },
                searchQuery = { query ->
                    viewModel.updateQueryProduct(query)
                },
                searchQueryProduct = searchQueryProduct
            )
        }

        if (showEdiDeleteDialog) {
            ProductOptionsDialog(
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
}

/*Tabla de Proveedores para seleccionar*/
@Composable
fun SelectSupplierTable(
    suppliers: LazyPagingItems<DetailedSupplierLocalModel>,
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
                "Buscar proveedor", fontSize = 28.sp, fontWeight = FontWeight.Bold,
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
                    count = suppliers.itemCount,
                    key = { index -> suppliers[index]?.id ?: index },
                    contentType = suppliers.itemContentType { "Suppliers" }
                ) { index ->

                    val supplier = suppliers[index]

                    if (supplier != null) {
                        SupplierItemTable(
                            supplier,
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
fun SupplierItemTable(
    supplier: DetailedSupplierLocalModel,
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
                supplier.company,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text("Número: ${supplier.id}", fontSize = 16.sp, color = Color.Gray)
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            GenericBlueUiButton(
                buttonText = "Seleccionar",
                onFunction = {
                    getValues.invoke(
                        supplier.company,
                        supplier.id
                    )
                }
            )
        }
    }
}