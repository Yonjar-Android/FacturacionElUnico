@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.productScreenTab.brandScreen.brandDetailScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.facturacionelunico.domain.models.BrandDomainModel
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.ProductItem
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun BrandDetailScreen(
    brandId: Long,
    brandName:String,
    navController: NavController,
    brandDetailScreenViewModel: BrandDetailScreenViewModel = hiltViewModel()
){
    LaunchedEffect(Unit) {
        brandDetailScreenViewModel.getProductsByBrand(brandId)
    }

    val products by brandDetailScreenViewModel.products.collectAsStateWithLifecycle()

    var textValue by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
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
                brandName, fontSize = 24.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(2f))
        }

        if (products.isNotEmpty()){
            LazyColumn {
                items(products) {
                    ProductItem(
                        it,
                        controller = navController
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(fraction = 0.9f),
            onClick = {
                showDialog = true
            }, colors = ButtonDefaults.buttonColors(containerColor = blueUi)
        ) {
            Text(
                "Editar Marca", fontWeight = FontWeight.Bold, fontSize = 24.sp,
                color = Color.White
            )
        }
    }

    if (showDialog){
        DialogCreateBrand(
            value = textValue,
            onValueChange = { textValue = it },
            dismiss = { showDialog = false },
            idBrand = brandId,
            viewModel = brandDetailScreenViewModel
        )
    }

}

@Composable
fun DialogCreateBrand(
    value: String,
    onValueChange: (String) -> Unit,
    idBrand: Long,
    dismiss: () -> Unit,
    viewModel: BrandDetailScreenViewModel
) {
    val context = LocalContext.current

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
            Text("Editar Marca", fontWeight = FontWeight.Bold, fontSize = 24.sp)

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
                    if (value.isEmpty()) {
                        Toast.makeText(context, "Ingrese un nombre", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    dismiss.invoke()
                    viewModel.updateBrand(
                        BrandDomainModel(
                            brandId = idBrand,
                            brandName = value
                        )
                    )
                }, colors = ButtonDefaults.buttonColors(containerColor = blueUi)
            ) {
                Text(
                    "Actualizar", fontWeight = FontWeight.Bold, fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}