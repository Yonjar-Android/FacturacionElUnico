package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.facturacionelunico.ObjetosDePrueba
import com.example.facturacionelunico.domain.models.ProductDomainModel
import com.example.facturacionelunico.presentation.sharedComponents.AddButton
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun ProductScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {

            var textValue by remember { mutableStateOf("") }
            SearchBarComponent(textValue, onChangeValue = { textValue = it })

            LazyColumn {
                items(ObjetosDePrueba.motorcycleProducts) {
                    ProductItem(it)
                }
            }
        }
        AddButton(
            modifier = Modifier.align(alignment = Alignment.BottomEnd)
        )
    }
}

@Composable
fun ProductItem(product: ProductDomainModel) {
        Row(modifier = Modifier.fillMaxWidth()
            .padding(15.dp)
            .clickable{

            },
            horizontalArrangement = Arrangement.SpaceBetween) {

            Column(modifier = Modifier.weight(0.6f)) {
                Text(
                    text = product.productName, fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )

                Text(

                    text = product.brand, fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            Column(modifier = Modifier.weight(0.3f), horizontalAlignment = Alignment.End) {
                Text(
                    text = "C$ ${product.price}", fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold, color = blueUi,
                    textAlign = TextAlign.End
                )

                Text(
                    text = "stock ${product.stock}", fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, color = Color.Gray,
                    textAlign = TextAlign.End,
                )
            }
        }
}

