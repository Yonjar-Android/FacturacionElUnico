package com.example.facturacionelunico.presentation.productScreenTab.productScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facturacionelunico.domain.models.DetailedProductModel
import com.example.facturacionelunico.presentation.sharedComponents.AddButton
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun ProductScreen(
    navController: NavController,
    productScreenViewModel: ProductScreenViewModel = hiltViewModel()
) {

    val products: LazyPagingItems<DetailedProductModel> = productScreenViewModel.products.collectAsLazyPagingItems()

    val searchQuery by productScreenViewModel.searchQuery.collectAsStateWithLifecycle()

    val message by productScreenViewModel.message.collectAsStateWithLifecycle()

    if (!message.isNullOrEmpty()){
        Toast.makeText(LocalContext.current,message,Toast.LENGTH_SHORT).show()
        productScreenViewModel.restartMessage()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {

            SearchBarComponent(searchQuery, onChangeValue = { newQuery ->
                productScreenViewModel.updateQuery(newQuery)
            })

            Spacer(modifier = Modifier.size(15.dp))

            LazyColumn {
                items(
                    count = products.itemCount,
                    key = { index -> products[index]?.id ?: index }
                ) { index ->

                    val product = products[index]

                    if(product != null){
                        ProductItem(product, navController)
                    }

                }
            }
        }
        AddButton(
            modifier = Modifier.align(alignment = Alignment.BottomEnd),
            functionClick = { navController.navigate("ProductCreateScreen") }
        )
    }

    // No realizar navegación hacia atrás desde esta pantalla
    BackHandler {}
}

@Composable
fun ProductItem(product: DetailedProductModel, controller: NavController) {
    Row(
        modifier = Modifier
            .clickable {
                controller.navigate("ProductDetailScreen/${product.id}")
            }
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(modifier = Modifier.weight(0.6f)) {
            Text(
                text = product.name, fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
            )

            Text(
                text = product.brand.toString(), fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Column(modifier = Modifier.weight(0.3f), horizontalAlignment = Alignment.End) {
            Text(
                text = "C$ ${product.salePrice}", fontSize = 20.sp,
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

