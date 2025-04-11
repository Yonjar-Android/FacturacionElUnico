@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.productScreenTab.brandScreen

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.facturacionelunico.ObjetosDePrueba
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.presentation.productScreenTab.categoryScreen.DialogCreateCategory
import com.example.facturacionelunico.presentation.sharedComponents.AddButton
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun BrandScreen() {

    var showDialog by remember { mutableStateOf(false) }
    var textValueBrand by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {
            var textValue by remember { mutableStateOf("") }
            SearchBarComponent(textValue, onChangeValue = { textValue = it })

            Spacer(modifier = Modifier.size(10.dp))

            LazyColumn {
                items(ObjetosDePrueba.motorcycleBrands) {
                    BrandItem(it)
                }
            }

            if (showDialog) {
                DialogCreateBrand(
                    value = textValueBrand,
                    dismiss = {
                        showDialog = false
                    },
                    onValueChange = { textValueBrand = it }
                )
            }
        }

        AddButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            functionClick = { showDialog = true })
    }
}

@Composable
fun BrandItem(brandItem: BrandDomainModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.6f),
            text = brandItem.brandName, fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.weight(0.1f))

        Button(
            modifier = Modifier.weight(0.3f),
            colors = ButtonDefaults.buttonColors(
                containerColor = blueUi
            ),
            onClick = {

            }
        ) {
            Text(
                text = "Ver productos", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
fun DialogCreateBrand(
    value: String,
    onValueChange: (String) -> Unit,
    dismiss: () -> Unit
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
            Text("Crear Marca", fontWeight = FontWeight.Bold, fontSize = 24.sp)

            Column {
                Text("Nombre de marca", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Spacer(modifier = Modifier.size(5.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value, onValueChange = { onValueChange(it) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(30.dp),
                    maxLines = 1,
                    singleLine = true
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
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
}