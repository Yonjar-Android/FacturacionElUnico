@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.buyScreen.purchaseDetail

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.domain.models.ProductItem
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.ClientText
import com.example.facturacionelunico.presentation.sellScreen.InvoiceTable
import com.example.facturacionelunico.presentation.sellScreen.SelectProductTable
import com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen.DialogConfirmProduct
import com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen.DialogFormPay
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PurchaseDetailScreen(
    purchaseId: Long,
    navController: NavController,
    viewModel: PurchaseDetailScreenViewModel = hiltViewModel()
){
    val context = LocalContext.current

    val products: LazyPagingItems<DetailedProductModel> = viewModel.products.collectAsLazyPagingItems()
    val productQuery by viewModel.searchQueryProduct.collectAsStateWithLifecycle()
    var quantity by remember { mutableStateOf("") }
    var showProductDialog by remember { mutableStateOf(false) }
    var showDialogConfirm by remember { mutableStateOf(false) }
    val productItem = remember { mutableStateOf(ProductItem(0, "", 0.0, 0)) }

    LaunchedEffect(purchaseId) {
        viewModel.getInvoiceDetail(purchaseId)
    }

    val invoice by viewModel.purchase.collectAsStateWithLifecycle()

    val message by viewModel.message.collectAsStateWithLifecycle()

    println("MENSAJE PANTALLA: $message")
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    var amount by remember { mutableStateOf("") }

    var productsTable by remember { mutableStateOf(mutableListOf<ProductItem>()) }

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
            if (invoice != null){
                if (invoice?.supplier != "Ninguno" && invoice!!.debt > 0){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        GenericBlueUiButton(
                            buttonText = "Pagar",
                            onFunction = { showDialog = true }
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

            InvoiceTable(productsTable)

            if (invoice?.supplier != "Ninguno"){
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
        SelectProductTable(
            products = products,
            closeTable = { showProductDialog = false },
            getValues = { name, id, precio ->
                showDialogConfirm = true
                productItem.value = ProductItem(id, name, precio, 0)
            },
            searchQuery = {
                viewModel.updateQueryProduct(it)
            },
            searchQueryProduct = productQuery
        )
    }

    if (showDialogConfirm){
        DialogConfirmProduct(
            value = quantity,
            onValueChange = { quantity = it },
            dismiss = { showDialogConfirm = false },
            onConfirm = {
                val exist = productsTable.any{ it.id == productItem.value.id }
                if (exist){
                    Toast.makeText(context, "El producto ya se encuentra en la tabla", Toast.LENGTH_SHORT).show()
                    return@DialogConfirmProduct
                }
                productsTable.add(productItem.value.copy(quantity = it))
                productsForUpdate.add(productItem.value.copy(quantity = it))
                showDialogConfirm = false
                showProductDialog = false
            })
    }

    if (showConfirmDialog){
        ConfirmPurchaseDialog(
            onConfirm = {
                viewModel.addProductsToPurchase(productsForUpdate)
            },
            onDismiss = { showConfirmDialog = false }
        )
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