package com.example.facturacionelunico.presentation.buyScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.facturacionelunico.presentation.buyScreen.purchaseHistory.PurchaseHistory

@Composable
fun BuyScreenTab(navController: NavController){
    Column(modifier = Modifier.fillMaxSize()) {
        var tabIndex by rememberSaveable { mutableIntStateOf(0) }
        val tabs = listOf("Realizar compra", "Compras")

        TabRow(
            selectedTabIndex = tabIndex
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, text = {Text(text = title)},
                    onClick = {tabIndex = index})
            }
        }

        when(tabIndex){
            0 -> {
                BuyScreen(navController = navController)
            }
            1 -> {
                PurchaseHistory(navController = navController)
            }
        }
    }
}