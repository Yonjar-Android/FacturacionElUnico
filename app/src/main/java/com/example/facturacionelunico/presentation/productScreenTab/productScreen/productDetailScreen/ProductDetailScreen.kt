package com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.facturacionelunico.presentation.sharedComponents.GenericBlueUiButton
import com.example.facturacionelunico.presentation.sharedComponents.TopAppBarCustom

@Composable
fun ProductDetailScreen(
    productId: Long,
    navController: NavController,
    productDetailViewModel: ProductDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        productDetailViewModel.loadProduct(productId)
    }

    val product by productDetailViewModel.product.collectAsStateWithLifecycle()

    val message by productDetailViewModel.message.collectAsStateWithLifecycle()

    if (!message.isNullOrEmpty()){
        Toast.makeText(LocalContext.current,message,Toast.LENGTH_SHORT).show()
        productDetailViewModel.restartMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior con título y flecha para navegar hacia atrás
        TopAppBarCustom(
            title = product?.name ?: "Producto",
            onNavigationClick = { navController.navigateUp() }
        )

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
            info = if (product?.description.isNullOrEmpty()) {
                "Ninguna"
            } else product!!.description
        )

        Spacer(modifier = Modifier.weight(1f))

        GenericBlueUiButton(
            buttonText = "Editar Producto",
            onFunction = {
                navController.navigate("ProductUpdateScreen/$productId")
            }
        )

    }
}

// Composable que muestra la información a detalle de los productos
// Mostrando el atributo del producto y su valor respectivo
@Composable
fun ProductDetailTexts(title: String, info: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.95f)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Text(
            info, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }


}