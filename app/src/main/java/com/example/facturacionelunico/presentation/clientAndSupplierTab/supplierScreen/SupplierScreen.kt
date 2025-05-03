@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.facturacionelunico.domain.models.SupplierDomainModel
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.TextFieldClient
import com.example.facturacionelunico.presentation.sharedComponents.AddButton
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun SupplierScreen(
    navController: NavController,
    viewModel: SupplierScreenViewModel = hiltViewModel()
) {

    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val message by viewModel.message.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    if (!message.isNullOrEmpty()){
        Toast.makeText(LocalContext.current,message,Toast.LENGTH_SHORT).show()
        if (!message!!.contains("Error")){
            showDialog = false
        }

        viewModel.restartMessage()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {

            SearchBarComponent(searchQuery, onChangeValue = { newQuery ->
                viewModel.updateQuery(newQuery)
            })

            Spacer(modifier = Modifier.size(15.dp))

            LazyColumn {
                items(suppliers) { supplier ->
                    SupplierItem(supplier, goToDetail = {
                        navController.navigate("SupplierDetailScreen/${supplier.id}")
                    })
                }
            }
        }
        AddButton(
            modifier = Modifier.align(alignment = Alignment.BottomEnd),
            functionClick = { showDialog = true }
        )
    }

    if (showDialog){
        SupplierDialog(
            title = "Añadir proveedor",
            textButton = "Añadir",
            dismiss = { showDialog = false },
            onConfirm = { supplier ->
                viewModel.createSupplier(supplier)
            }
        )
    }

    // No realizar navegación hacia atrás desde esta pantalla
    BackHandler {}
}

// Función para mostrar información de un proveedor
@Composable
fun SupplierItem(supplier: SupplierDomainModel, goToDetail: () -> Unit){
    Row(
        modifier = Modifier
            .clickable{
                // Función para navegar al detalle del proveedor
                goToDetail.invoke()
            }
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(supplier.contactName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                text = supplier.company,
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray
            )

        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text("C$ Dinero", fontWeight = FontWeight.Bold,
                fontSize = 20.sp)

            Text("Deuda", fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp, color = Color.Gray)
        }
    }
}

// Dialog que muestra todos los campos necesarios para crear un proveedor
@Composable
fun SupplierDialog(
    title: String,
    textButton: String,
    dismiss: () -> Unit,
    onConfirm: (SupplierDomainModel) -> Unit
){

    var company by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

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
                onValueChange = { company = it }
            )

            TextFieldClient(
                title = "Nombre de Contacto",
                value = contactName,
                onValueChange = { contactName = it }
            )

            TextFieldClient(
                title = "Teléfono",
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextFieldClient(
                title = "Correo electrónico",
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            TextFieldClient(
                title = "Dirección",
                value = address,
                onValueChange = { address = it }
            )

            Button(
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                onClick = {
                    val supplier = SupplierDomainModel(
                        company = company,
                        contactName = contactName,
                        phone = phoneNumber,
                        email = email,
                        address = address,
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
