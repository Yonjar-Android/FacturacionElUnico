@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.facturacionelunico.domain.models.supplier.SupplierDomainModel
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.ClientText
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.FacturaItem
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.TextFieldClient
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun SupplierDetailScreen(
    supplierId: Long,
    navController: NavController,
    viewModel: SupplierDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(supplierId) {
        viewModel.getSupplierById(supplierId)
    }

    val supplier by viewModel.supplier.collectAsStateWithLifecycle()

    val message by viewModel.message.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    // Campos para actualizar proveedor

    var company by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    LaunchedEffect(supplier) {
        company = supplier?.company.toString()
        contactName = supplier?.contactName ?: ""
        email = supplier?.email ?: ""
        phoneNumber = supplier?.phone.toString()
        address = supplier?.address ?: ""
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
                title = "Proveedor",
                onNavigationClick = { navController.navigateUp() }
            )
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                GenericBlueUiButton(
                    buttonText = "Editar proveedor",
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
            ClientText(title = "Nombre de Empresa", value = supplier?.company.toString())

            ClientText(title = "Nombre de Contacto", value = supplier?.contactName.toString())

            ClientText(title = "Teléfono", value = supplier?.phone.toString())

            ClientText(
                title = "Correo",
                value = if (supplier?.email.isNullOrEmpty()) "Sin correo" else supplier?.email.toString()
            )

            ClientText(
                title = "Dirección",
                value = if (supplier?.address.isNullOrEmpty()) "Sin dirección" else supplier?.address.toString()
            )

            ClientText(title = "Deuda", value = "C$ ${supplier?.debt}")

            Text(text = "Compras", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.size(10.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (supplier?.purchases != null && supplier?.purchases?.isNotEmpty() == true) {
                    items(supplier!!.purchases) {
                        FacturaItem(it.purchaseId, "Compra #${it.purchaseId}", it.state, {
                            navController.navigate("PurchaseDetailScreen/${it}")
                        })
                    }
                }
            }
        }
    }

    if (showDialog) {
        SupplierDetailDialog(
            title = "Editar proveedor",
            textButton = "Actualizar",
            dismiss = { showDialog = false },
            onConfirm = { supplier ->
                viewModel.updateSupplier(
                    supplier
                        .copy(id = supplierId)
                )
            },
            email = email,
            company = company,
            contactName = contactName,
            phoneNumber = phoneNumber,
            address = address,
            onCompanyChange = { company = it },
            onContactNameChange = { contactName = it },
            onEmailChange = { email = it },
            onPhoneNumberChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*$"))) {
                    phoneNumber = it
                }
            },
            onAddressChange = { address = it }
        )
    }

}

@Composable
fun SupplierDetailDialog(
    title: String,
    textButton: String,
    dismiss: () -> Unit,
    onConfirm: (SupplierDomainModel) -> Unit,
    email: String,
    company: String,
    contactName: String,
    phoneNumber: String,
    address: String,
    onCompanyChange: (String) -> Unit,
    onContactNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
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
                title = "Nombre de Empresa",
                value = company,
                onValueChange = { onCompanyChange.invoke(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextFieldClient(
                title = "Nombre de Contacto",
                value = contactName,
                onValueChange = { onContactNameChange.invoke(it) }
            )

            TextFieldClient(
                title = "Teléfono",
                value = phoneNumber,
                onValueChange = { onPhoneNumberChange.invoke(it) }
            )

            TextFieldClient(
                title = "Correo",
                value = email,
                onValueChange = { onEmailChange.invoke(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextFieldClient(
                title = "Dirección",
                value = address,
                onValueChange = { onAddressChange.invoke(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            Button(
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                onClick = {
                    val supplier = SupplierDomainModel(
                        company = company,
                        contactName = contactName,
                        phone = phoneNumber,
                        email = email,
                        address = address
                    )
                    // Llamada a la función para crear un proveedor
                    onConfirm.invoke(supplier)
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