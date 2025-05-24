@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.example.facturacionelunico.domain.models.client.ClientDomainModel
import com.example.facturacionelunico.domain.models.client.DetailedClientLocalModel
import com.example.facturacionelunico.presentation.sharedComponents.AddButton
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun ClientScreen(
    viewModel: ClientScreenViewModel = hiltViewModel(),
    navController: NavController
) {

    val clients: LazyPagingItems<DetailedClientLocalModel> =
        viewModel.clients.collectAsLazyPagingItems()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val message by viewModel.message.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    if (!message.isNullOrEmpty()) {
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()

        if (!message!!.contains("Error")) {
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

            Spacer(modifier = Modifier.size(25.dp))

            LazyColumn {
                items(
                    count = clients.itemCount,
                    key = { index -> clients[index]?.id ?: index },
                    contentType = clients.itemContentType { "Clients" }
                ) { index ->

                    val client = clients[index]

                    if (client != null) {
                        ClientItem(client, goToDetail = {
                            navController.navigate("ClientDetailScreen/${client.id}")
                        })
                    }
                }
            }
        }
        AddButton(
            modifier = Modifier.align(alignment = Alignment.BottomEnd),
            functionClick = { showDialog = true }
        )
    }

    if (showDialog) {
        ClientDialog(
            title = "Añadir cliente",
            textButton = "Añadir",
            dismiss = { showDialog = false },
            onConfirm = { client ->
                viewModel.createClient(client)
            }
        )
    }

    // No realizar navegación hacia atrás desde esta pantalla
    BackHandler {}
}

// Función para mostrar información de un cliente
@Composable
fun ClientItem(client: DetailedClientLocalModel, goToDetail: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable {
                // LLamado a la función para navegar al detalle del cliente
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
            val fullName = "${client.name} ${client.lastName}"
            Text(fullName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                text = if (client.phone.isBlank()) "Ninguno" else client.phone,
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray
            )

        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text("C$ ${client.deptTotal}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Deuda", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray)
        }
    }
}

// Dialog que muestra todos los campos necesarios para crear un cliente
@Composable
fun ClientDialog(
    title: String,
    textButton: String,
    dismiss: () -> Unit,
    onConfirm: (ClientDomainModel) -> Unit
) {

    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

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
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*$"))) {
                        code = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            TextFieldClient(
                title = "Nombre",
                value = name,
                onValueChange = { name = it }
            )

            TextFieldClient(
                title = "Apellido",
                value = lastname,
                onValueChange = { lastname = it }
            )

            TextFieldClient(
                title = "Teléfono",
                value = phoneNumber,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*$"))) {
                        phoneNumber = it
                    }
                },
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

@Composable
fun TextFieldClient(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Spacer(modifier = Modifier.size(5.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { onValueChange(it) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(30.dp),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = keyboardOptions
        )

        Spacer(modifier = Modifier.size(5.dp))
    }
}