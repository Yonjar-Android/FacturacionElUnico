@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.buyScreen.purchaseDetail

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.facturacionelunico.presentation.buyScreen.SelectProductTablePurchase
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.ClientText
import com.example.facturacionelunico.presentation.sellScreen.ProductOptionsDialog
import com.example.facturacionelunico.presentation.sellScreen.SelectProductTable
import com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen.DialogConfirmProduct
import com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen.DialogFormPay
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom
import com.example.facturacionelunico.utils.validations.ValidationFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PurchaseDetailScreen(
    purchaseId: Long,
    navController: NavController,
    viewModel: PurchaseDetailScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()

    val products: LazyPagingItems<DetailedProductModel> =
        viewModel.products.collectAsLazyPagingItems()
    val productQuery by viewModel.searchQueryProduct.collectAsStateWithLifecycle()
    var quantity by remember { mutableStateOf("") }
    var showProductDialog by remember { mutableStateOf(false) }
    var showDialogConfirm by remember { mutableStateOf(false) }
    val productItem = remember {
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

    LaunchedEffect(purchaseId) {
        viewModel.getInvoiceDetail(purchaseId)
    }


    val invoice by viewModel.purchase.collectAsStateWithLifecycle()

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
                title = "Compra",
                onNavigationClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            if (invoice != null) {
                if (invoice?.supplier != "Ninguno" && invoice!!.debt > 0) {
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

            ClientText(title = "N° Factura", value = invoice?.id.toString())

            ClientText(title = "Proveedor", value = invoice?.supplier ?: "")

            ClientText(title = "Total", value = "${invoice?.total} C$")

            ClientText(title = "Debe", value = "${invoice?.debt} C$")

            ClientText(title = "Productos", value = "")

            InvoiceTableDetail(productsTable, showDialog = { quantity, product ->
                showEdiDeleteDialog = true
                quantityToModify = quantity
                productToModify = product
            })

            if (invoice?.supplier != "Ninguno") {
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
                viewModel.payInvoice(purchaseId, it)
            })
    }

    if (showProductDialog) {
        SelectProductTablePurchase(
            products = products,
            closeTable = { showProductDialog = false },
            getValues = { name, id, precio, precioCompra ->
                showDialogConfirm = true
                productItem.value = ProductItem(
                    id = id,
                    name = name,
                    price = precioCompra,
                    quantity = 0,
                    purchasePrice = precioCompra
                )
                println(productItem.value)
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
            onConfirm = { quantityOfProduct ->
                val exist = productsTable.any { it.id == productItem.value.id }
                if (exist) {
                    Toast.makeText(
                        context,
                        "El producto ya se encuentra en la tabla",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@DialogConfirmProduct
                }

                // Agregar nuevo producto y actualizar estado de la ui
                productsTable = productsTable.toMutableList().apply {
                    add(productItem.value.copy(quantity = quantityOfProduct))
                }

                productsForUpdate.add(productItem.value.copy(quantity = quantityOfProduct))

                quantity = ""
                showDialogConfirm = false
                showProductDialog = false
            })
    }

    if (showConfirmDialog) {
        ConfirmPurchaseDialog(
            onConfirm = {
                viewModel.addProductsToPurchase(productsForUpdate)
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showEdiDeleteDialog) {
        ProductOptionsDialog(
            context = context,
            currentQuantity = quantityToModify,
            onEditClick = { quantity ->
                showEdiDeleteDialog = false

                val oldTable = productsTable.toList()  // Copia inmutable (segura)

                coroutine.launch {

                    // Modificar el producto en la tabla con su cantidad
                    val modifiedTable = productsTable.toMutableList().apply {
                        removeIf { it.id == productToModify.id }
                        add(productToModify.copy(quantity = quantity))
                    }

                    val message = viewModel.updateProduct(
                        productToModify.copy(quantity = quantity),
                        modifiedTable.sumOf { it.subtotal } // Calcular nuevo total de la factura
                    )

                    if (message == "Error: El nuevo total es menor a la cantidad ya abonada") {
                        productsTable = oldTable.toMutableList() // Restaurar en caso de error
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                quantityToModify = 0
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

                // Lanzar corrutina para obtener el mensaje de la función asíncrona
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

                    // Si no hubo error, actualizar las tablas de ui y la de productos a agregar a la factura

                    val oldProductsTable = productsTable.toList() // Copia inmutable (segura)

                    productsTable.toMutableList().apply {
                        removeIf { it.id == productToModify.id }
                        productsTable = this
                    }

                    val message1 =
                        viewModel.deleteProduct(productToModify, totalSinProductoModificado)

                    if (message1 == "Error: El nuevo total es menor a la cantidad ya abonada") {
                        productsTable = oldProductsTable.toMutableList() // Restaurar
                        Toast.makeText(context, message1, Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    showEdiDeleteDialog = false
                }
            })
    }
}

@Composable
fun ConfirmPurchaseDialog(
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
                Text(text = "Confirmar compra", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "¿Confirmas la compra de los productos agregados?")
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


@Composable
fun InvoiceTableDetail(productList: List<ProductItem>, showDialog: (Int, ProductItem) -> Unit) {
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
                        showDialog(product.quantity, product)
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