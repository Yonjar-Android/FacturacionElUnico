package com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun ProductDetailScreen(
    productId: Long,
    navController: NavController,
    productDetailViewModel: ProductDetailViewModel = hiltViewModel()
){

    LaunchedEffect(Unit) {
        productDetailViewModel.loadProduct(productId)
    }

    val product by productDetailViewModel.product.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
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
                product?.name ?: "Producto", fontSize = 24.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(2f))
        }

        ProductDetailTexts(
            title = "Id del producto:",
            info = product?.id.toString()
        )

        ProductDetailTexts(
            title = "Stock:",
            info = product?.stock.toString()
        )

        ProductDetailTexts(
            title = "Precio Venta:",
            info = "C$ ${product?.salePrice.toString()}"
        )

        ProductDetailTexts(
            title = "Precio Compra:",
            info = "C$ ${product?.salePrice.toString()}"
        )

        ProductDetailTexts(
            title = "Marca:",
            info = product?.brand ?: "Sin marca"
        )

        ProductDetailTexts(
            title = "Categoría:",
            info = product?.category ?: "Sin categoría"
        )

        ProductDetailTexts(
            title = "Descripción:",
            info = if (product?.description.isNullOrEmpty()){
                "Ninguna"
            } else product!!.description
        )

    }
}

@Composable
fun ProductDetailTexts(title:String, info:String){
    Column(
        modifier = Modifier.fillMaxWidth(fraction = 0.95f)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center)

        Text(info,fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center)
    }



}