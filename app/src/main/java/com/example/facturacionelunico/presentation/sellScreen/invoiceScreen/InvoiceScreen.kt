package com.example.facturacionelunico.presentation.sellScreen.invoiceScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
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
import com.example.facturacionelunico.domain.models.invoice.InvoiceDomainModel

@Composable
fun InvoiceScreen(
    navController: NavController,
    viewModel: InvoiceScreenViewModel = hiltViewModel()
){

    val invoices: LazyPagingItems<InvoiceDomainModel> = viewModel.invoices.collectAsLazyPagingItems()

    val dept = listOf<String>("Todas", "Pendientes")
    var deptSelectedOption by remember { mutableStateOf(dept[0]) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.size(10.dp))

        Text(text = "Facturas", fontSize = 32.sp, fontWeight = FontWeight.Bold)

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
                    Text(text = dept, color = Color.Black)
                }
            }
        }

        LazyColumn {
            items(
                count = invoices.itemCount,
                key = { index -> invoices[index]?.id ?: index }
            ) { index ->

                val it = invoices[index]

                if(it != null){
                    FacturaItem(
                        id = it.id,
                        title = "Factura $${it.id}",
                        state = it.state,
                        total = it.total,
                        goToDetail = {
                            navController.navigate("InvoiceDetailScreen/${it}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FacturaItem(
    id: Long,
    title: String,
    state:String,
    total: Double,
    goToDetail: (Long) -> Unit ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                goToDetail.invoke(id)
            }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp,
            color = if (state == "PENDIENTE") Color.Red else Color(0XFF338822)
        )
        Text(
            text = "Ver detalles >>", fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
            color = if (state == "PENDIENTE") Color.Red else Color(0XFF338822)
        )

        Text(
            text = "Total: $total", fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
        )

    }
    HorizontalDivider()
}
