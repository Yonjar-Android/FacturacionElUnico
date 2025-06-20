package com.example.facturacionelunico.presentation.buyScreen.purchaseHistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facturacionelunico.domain.models.purchase.PurchaseDomainModel
import com.example.facturacionelunico.presentation.sellScreen.invoiceScreen.FacturaItem

@Composable
fun PurchaseHistory(
    navController: NavController,
    viewModel: PurchaseHistoryViewModel = hiltViewModel()
) {
    val purchases: LazyPagingItems<PurchaseDomainModel> = viewModel.purchases.collectAsLazyPagingItems()

    val dept = listOf<String>("Todas", "Pendientes")
    var deptSelectedOption by remember { mutableStateOf(dept[0]) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.size(10.dp))

        Text(text = "Compras", fontSize = 32.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dept.forEach { dept ->
                Row(
                    Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = deptSelectedOption == dept,
                        onClick = {
                            deptSelectedOption = dept
                            viewModel.getInvoicesBySelectedOption(dept)
                        },
                    )
                    Text(text = dept, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        LazyColumn {
            items(
                count = purchases.itemCount,
                key = { index -> purchases[index]?.purchaseId ?: index }
            ) { index ->

                val it = purchases[index]

                if(it != null){
                    FacturaItem(
                        id = it.purchaseId,
                        title = "Compras $${it.purchaseId}",
                        state = it.state,
                        total = it.total,
                        goToDetail = {
                            navController.navigate("PurchaseDetailScreen/${it}")
                        }
                    )
                }
            }
        }
    }
}