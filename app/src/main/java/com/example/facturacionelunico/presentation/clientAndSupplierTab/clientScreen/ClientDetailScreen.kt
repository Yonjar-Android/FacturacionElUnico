@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun ClientDetailScreen(
    clientId: Long,
    navController: NavController,
    viewModel: ClientDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(clientId) {
        viewModel.getClientById(clientId)
    }

    val client by viewModel.client.collectAsStateWithLifecycle()

    val invoices: LazyPagingItems<InvoiceDomainModel> = viewModel.invoices.collectAsLazyPagingItems()

    val id:Long? = client?.id

    val message by viewModel.message.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    // Campos para actualizar cliente

    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }


    LaunchedEffect(client) {
        code = client?.numberIdentifier.toString()
        name = client?.name ?: ""
        lastname = client?.lastName ?: ""
        phoneNumber = client?.phone.toString()
    }

    if (!message.isNullOrEmpty()) {
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()

        if (!message!!.contains("Error")) {
            showDialog = false
        }

        viewModel.restartMessage()
    }


    Scaffold(
        topBar = {
            TopAppBarCustom(
                title = "Cliente",
                onNavigationClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                GenericBlueUiButton(
                    buttonText = "Editar Cliente",
                    onFunction = { showDialog = true }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClientText(title = "Número de cliente", value = client?.numberIdentifier.toString())

            val fullName = "${client?.name} ${client?.lastName}"
            ClientText(title = "Nombre", value = fullName)

            ClientText(
                title = "Teléfono",
                value = if (client?.phone.isNullOrEmpty()) "Ninguno" else client?.phone.toString()
            )

            ClientText(title = "Deuda", value = "C$ ${client?.deptTotal}")

            Spacer(modifier = Modifier.size(10.dp))

            Text(text = "Facturas", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.size(10.dp))

            // Cargar facturas
            if (invoices.itemCount != 0){
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        count = invoices.itemCount,
                        key = { index -> invoices[index]?.id ?: index },
                        contentType = { "Invoices" }
                    ){
                        val invoice = invoices[it]

                        if (invoice != null){
                            FacturaItem(invoice.id,"Factura #${invoice.id}", invoice.state,
                                goToDetail = {
                                    navController.navigate("InvoiceDetailScreen/${it}")
                                })
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ClientDetailDialog(
            title = "Editar cliente",
            textButton = "Actualizar",
            dismiss = { showDialog = false },
            code = code,
            onCodeChange = { code = it },
            name = name,
            onNameChange = { name = it },
            lastname = lastname,
            onLastNameChange = { lastname = it },
            phoneNumber = phoneNumber,
            onPhoneNumberChange = { phoneNumber = it },
            onConfirm = { client ->
                 if (id != null) viewModel.updateClient(client.copy(id = id))
            })
    }
}

@Composable
fun ClientText(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

    }
}

@Composable
fun FacturaItem(
    id: Long,
    title: String,
    state:String,
    goToDetail: (Long) -> Unit ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                goToDetail.invoke(id)
            }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp,
            color = if (state == "PENDIENTE") Color.Red else Color(0XFF338822)
        )
        Text(
            text = "Ver detalles >>", fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
            color = if (state == "PENDIENTE") Color.Red else Color(0XFF338822)
        )

    }
    HorizontalDivider()
}

@Composable
fun ClientDetailDialog(
    title: String,
    textButton: String,
    dismiss: () -> Unit,
    onConfirm: (ClientDomainModel) -> Unit,
    code: String,
    name: String,
    lastname: String,
    phoneNumber: String,
    onCodeChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
) {

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
            Text(title, fontWeight = FontWeight.Bold, fontSize = 24.sp)

            TextFieldClient(
                title = "Código o Número",
                value = code,
                onValueChange = { onCodeChange.invoke(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextFieldClient(
                title = "Nombre",
                value = name,
                onValueChange = { onNameChange.invoke(it) }
            )

            TextFieldClient(
                title = "Apellido",
                value = lastname,
                onValueChange = { onLastNameChange.invoke(it) }
            )

            TextFieldClient(
                title = "Teléfono",
                value = phoneNumber,
                onValueChange = { onPhoneNumberChange.invoke(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                onClick = {
                    val client = ClientDomainModel(
                        name = name,
                        lastName = lastname,
                        phone = phoneNumber,
                        numberIdentifier = if (code.isEmpty()) 0 else code.toInt()
                    )
                    // Llamada a la función para crear un cliente
                    onConfirm.invoke(client)
                },
                colors = ButtonDefaults.buttonColors(containerColor = blueUi)
            ) {
                Text(
                    textButton,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}