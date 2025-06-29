@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.presentation.buyScreen.purchaseDetail.InvoiceTableDetail
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.ClientText
import com.example.facturacionelunico.presentation.sellScreen.ProductOptionsDialog
import com.example.facturacionelunico.presentation.sellScreen.SelectProductTable
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom
import com.example.facturacionelunico.ui.theme.blueUi
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
@Composable
fun InvoiceDetailScreen(
    invoiceId: Long,
    navController: NavController,
    viewModel: InvoiceDetailViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    val products: LazyPagingItems<DetailedProductModel> =
        viewModel.products.collectAsLazyPagingItems()
    val productQuery by viewModel.searchQueryProduct.collectAsStateWithLifecycle()
    var quantity by remember { mutableStateOf("") }
    var showProductDialog by remember { mutableStateOf(false) }
    var showDialogConfirm by remember { mutableStateOf(false) }
    val productItem =
        remember {
            mutableStateOf(
                ProductItem(
                    id = 0,
                    name = "",
                    price = 0.0,
                    quantity = 0,
                    purchasePrice = 0.0
                )
            )
        }

    LaunchedEffect(invoiceId) {
        viewModel.getInvoiceDetail(invoiceId)
    }

    val invoice by viewModel.invoice.collectAsStateWithLifecycle()

    val message by viewModel.message.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    var amount by remember { mutableStateOf("") }

    var productsTable by remember { mutableStateOf(mutableListOf<ProductItem>()) }

    var quantityToModify by remember { mutableIntStateOf(0) }
    var productToModify by remember {
        mutableStateOf(
            ProductItem(
                detailId = 0,
                id = 0,
                name = "",
                price = 0.0,
                quantity = 0,
                purchasePrice = 0.0
            )
        )
    }
    var showEdiDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(invoice?.products) {
        productsTable = invoice?.products?.toMutableList() ?: mutableListOf()
    }

    var productsForUpdate by remember {
        mutableStateOf(
            mutableListOf<ProductItem>()
        )
    }

    if (!message.isNullOrEmpty()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if (message?.contains("Error") == false) {
            showDialog = false
            showConfirmDialog = false
            amount = ""
            productsForUpdate.clear()
        }
        viewModel.restartMessage()
    }

    Scaffold(
        topBar = {
            TopAppBarCustom(
                title = "Factura",
                onNavigationClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            if (invoice != null) {
                if (invoice?.clientName != "Ninguno" && invoice!!.debt > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        GenericBlueUiButton(
                            buttonText = "Pagar",
                            onFunction = { showDialog = true },
                            enabled = productsForUpdate.isEmpty()
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ClientText(title = "N° Factura", value = invoice?.invoiceId.toString())

            ClientText(title = "Cliente", value = invoice?.clientName ?: "")

            ClientText(title = "Total", value = "${invoice?.total} C$")

            ClientText(title = "Debe", value = "${invoice?.debt} C$")

            ClientText(title = "Productos", value = "")

            InvoiceTableDetail(
                productsTable,
                showDialog = { quantity, product ->
                    showEdiDeleteDialog = true
                    quantityToModify = quantity
                    productToModify = product
                })

            if (invoice?.clientName != "Ninguno") {
                GenericBlueUiButton(
                    buttonText = "Agregar producto",
                    onFunction = { showProductDialog = true }
                )

                GenericBlueUiButton(
                    buttonText = "Confirmar nuevos productos",
                    onFunction = {
                        showConfirmDialog = true
                    },
                    enabled = productsForUpdate.isNotEmpty()
                )
            }
        }
        Spacer(modifier = Modifier.size(30.dp))
    }

    if (showDialog) {
        DialogFormPay(
            value = amount,
            onValueChange = { amount = it },
            dismiss = { showDialog = false },
            onConfirm = {
                viewModel.payInvoice(invoiceId, it)
            })
    }

    if (showProductDialog) {
        SelectProductTable(
            products = products,
            closeTable = { showProductDialog = false },
            getValues = { name, id, precio, precioNoUsar ->
                showDialogConfirm = true
                productItem.value =
                    ProductItem(
                        id = id,
                        name = name,
                        price = precio,
                        quantity = 0,
                        purchasePrice = precioNoUsar
                    )
            },
            searchQuery = {
                viewModel.updateQueryProduct(it)
            },
            searchQueryProduct = productQuery
        )
    }

    if (showDialogConfirm) {
        DialogConfirmProduct(
            value = quantity,
            onValueChange = { quantity = it },
            dismiss = { showDialogConfirm = false },
            onConfirm = { quantityForProduct ->
                val exist = productsTable.any { it.id == productItem.value.id }
                if (exist) {
                    Toast.makeText(
                        context,
                        "El producto ya se encuentra en la tabla",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@DialogConfirmProduct
                }

                productsTable.add(productItem.value.copy(quantity = quantityForProduct))
                productsForUpdate.add(productItem.value.copy(quantity = quantityForProduct))

                quantity = "0"
                showDialogConfirm = false
                showProductDialog = false
            })
    }

    if (showConfirmDialog) {
        ConfirmDialog(
            onConfirm = {
                viewModel.addProductsToInvoice(productsForUpdate)
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showEdiDeleteDialog) {
        ProductOptionsDialog(
            context = context,
            currentQuantity = quantityToModify,
            onEditClick = {
                showEdiDeleteDialog = false

                val oldTableProducts = productsTable.toList() // Copia inmutable (segura)

                productsTable.toMutableList().apply {
                    val index = indexOfFirst { it.id == productToModify.id }
                    if (index != -1) {
                        val updatedItem = this[index].copy(quantity = it)
                        this[index] = updatedItem
                        productsTable = this // Esto actualiza el estado y dispara recomposición
                    }
                }

                coroutine.launch {
                    val message = viewModel.updateProduct(
                        productToModify.copy(quantity = it),
                        productsTable.sumOf { it.subtotal }
                    )

                    if (message == "Error: No hay suficiente stock para realizar la actualización" || message == "Error: El nuevo total es menor a la cantidad ya abonada") {
                        productsTable = oldTableProducts.toMutableList()
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    quantityToModify = 0
                }

            },
            onDismiss = {
                showEdiDeleteDialog = false
            },
            onDeleteClick = {

                if (productsTable.count() == 1) {
                    Toast.makeText(
                        context,
                        "No puedes eliminar todos los productos de la factura",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@ProductOptionsDialog
                }

                val tableDifference = productsTable.count() - productsForUpdate.count()

                if (productToModify !in productsForUpdate && tableDifference == 1) {
                    Toast.makeText(
                        context,
                        "No puedes eliminar todos los productos agregados con anterioridad",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@ProductOptionsDialog
                }


                // Cálculo del nuevo total
                val totalSinProductoModificado = productsTable
                    .filter { it.id != productToModify.id }
                    .sumOf { it.subtotal }

                coroutine.launch {

                    /* Si el producto a modificar está en la lista de productos a actualizar, entonces lo removemos de la tabla y no llamamos a la función de eliminar
                    ya que no se encuentra en la base de datos */
                    if (productToModify in productsForUpdate) {
                        productsForUpdate.remove(productToModify)
                        showEdiDeleteDialog = false

                        productsTable.toMutableList().apply {
                            removeIf { it.id == productToModify.id }
                            productsTable = this
                        }
                        return@launch
                    }

                    val oldProductsTable = productsTable.toList() // Copia inmutable (segura)

                    // Remover el item a eliminar de la tabla para luego calcular el nuevo total
                    productsTable.toMutableList().apply {
                        remove(productToModify)
                        productsTable = this
                    }

                   val message = viewModel.deleteProduct(
                        productToModify,
                        totalSinProductoModificado
                    ) // Cálculo del nuevo total

                    if (message == "Error: El nuevo total es menor a la cantidad ya abonada") {
                        productsTable = oldProductsTable.toMutableList() // Restaurar
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    showEdiDeleteDialog = false
                }
            })
    }
}

@Composable
fun DialogFormPay(
    value: String,
    onValueChange: (String) -> Unit,
    dismiss: () -> Unit,
    onConfirm: (amount: Double) -> Unit
) {
    val context = LocalContext.current


    BasicAlertDialog(
        onDismissRequest = dismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Ingresar dinero a pagar", fontWeight = FontWeight.Bold, fontSize = 24.sp)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Monto", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Spacer(modifier = Modifier.size(5.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            onValueChange(it)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(30.dp),
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                    onClick = {
                        if (value.isEmpty()) {
                            Toast.makeText(context, "Ingrese el monto a pagar", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (value.toDouble() <= 0) {
                            Toast.makeText(
                                context,
                                "Ingrese un valor mayor que 0",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return@Button
                        }
                        onConfirm(value.toDouble())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = blueUi)
                ) {
                    Text(
                        "Pagar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DialogConfirmProduct(
    value: String,
    onValueChange: (String) -> Unit,
    dismiss: () -> Unit,
    onConfirm: (quantity: Int) -> Unit
) {
    val context = LocalContext.current


    BasicAlertDialog(
        onDismissRequest = dismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Ingresar cantidad del producto", fontWeight = FontWeight.Bold, fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cantidad", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Spacer(modifier = Modifier.size(5.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*$"))) {
                            onValueChange(it)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(30.dp),
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                    onClick = {
                        if (value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Ingrese la cantidad del producto",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return@Button
                        }
                        if (value.toDouble() <= 0) {
                            Toast.makeText(
                                context,
                                "Ingrese un valor mayor que 0",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return@Button
                        }
                        onConfirm(value.toInt())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = blueUi)
                ) {
                    Text(
                        "Confirmar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Confirmar venta", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "¿Confirmas la venta de los productos agregados?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    TextButton(onClick = onConfirm) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}
