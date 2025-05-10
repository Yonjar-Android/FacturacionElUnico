@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.ClientText
import com.example.facturacionelunico.presentation.sellScreen.InvoiceTable
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun InvoiceDetailScreen(
    invoiceId: Long,
    navController: NavController,
    viewModel: InvoiceDetailViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(invoiceId) {
        viewModel.getInvoiceDetail(invoiceId)
    }

    val invoice by viewModel.invoice.collectAsStateWithLifecycle()

    val message by viewModel.message.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    var amount by remember { mutableStateOf("") }

    if (!message.isNullOrEmpty()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if(message?.contains("Error") == false){
            showDialog = false
            amount = ""
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ClientText(title = "N° Factura", value = invoice?.invoiceId.toString())

            ClientText(title = "Cliente", value = invoice?.clientName ?: "")

            ClientText(title = "Total", value = "${invoice?.total} C$")

            ClientText(title = "Debe", value = "${invoice?.debt} C$")

            ClientText(title = "Productos", value = "")

            InvoiceTable(invoice?.products ?: emptyList())

            GenericBlueUiButton(
                buttonText = "Agregar producto",
                onFunction = { }
            )
        }
    }

    if (showDialog) {
        DialogFormPay(
            value = amount,
            onValueChange = { amount = it},
            dismiss = { showDialog = false },
            onConfirm = {
                viewModel.payInvoice(invoiceId,it)
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
                            Toast.makeText(context, "Ingrese un valor mayor que 0", Toast.LENGTH_SHORT)
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