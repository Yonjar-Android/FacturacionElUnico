package com.example.facturacionelunico.presentation.productScreenTab.productScreen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun ProductCreateScreen(navController: NavController) {

    var productName by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var priceSell by remember { mutableStateOf("") }
    var priceBuy by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "arrow back icon"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Registrar Nuevo Producto", fontSize = 24.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(2f))
        }

        Spacer(modifier = Modifier.size(5.dp))

        TextFieldComponent(
            textFieldName = "Nombre del Producto",
            textValue = productName,
            onTextValueChange = { productName = it }
        )

        TextFieldDrawer(
            textFieldName = "Categoría",
            textValue = "",
            onTextValueChange = {}
        )

        TextFieldDrawer(
            textFieldName = "Marca",
            textValue = "",
            onTextValueChange = {}
        )

        TextFieldDescription(
            textFieldName = "Descripción",
            textValue = description,
            onTextValueChange = { description = it }
        )

        TextFieldComponent(
            textFieldName = "Stock",
            textValue = stock.toString(),
            onTextValueChange = { stock = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextFieldComponent(
            textFieldName = "Precio Venta",
            textValue = priceSell.toString(),
            onTextValueChange = { priceSell = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        TextFieldComponent(
            textFieldName = "Precio Compra",
            textValue = priceBuy.toString(),
            onTextValueChange = { priceBuy = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Button(
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onClick = {

            }, colors = ButtonDefaults.buttonColors(containerColor = blueUi)
        ) {
            Text(
                "Guardar", fontWeight = FontWeight.Bold, fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TextFieldComponent(
    textFieldName: String,
    textValue: String,
    onTextValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(fraction = 0.85f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(textFieldName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.size(5.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            value = textValue, onValueChange = { onTextValueChange(it) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(30.dp),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = keyboardOptions
        )
    }

    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun TextFieldDrawer(
    textFieldName: String,
    textValue: String,
    onTextValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(fraction = 0.85f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(textFieldName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.size(5.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            value = textValue, onValueChange = { onTextValueChange(it) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(30.dp),
            maxLines = 1,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "arrow drop down icon"
                )
            }
        )
    }
}

@Composable
fun TextFieldDescription(
    textFieldName: String,
    textValue: String,
    onTextValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(fraction = 0.85f),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(textFieldName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.size(5.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .height(100.dp),
            value = textValue, onValueChange = { onTextValueChange(it) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(30.dp),
            maxLines = 3,
        )
    }
}