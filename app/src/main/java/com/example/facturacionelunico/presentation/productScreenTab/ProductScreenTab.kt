package com.example.facturacionelunico.presentation.productScreenTab

import android.content.Context
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
import com.example.facturacionelunico.presentation.productScreenTab.brandScreen.BrandScreen
import com.example.facturacionelunico.presentation.productScreenTab.categoryScreen.CategoryScreen
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.ProductScreen

@Composable
fun ProductScreenTab(navController: NavController){
    Column(modifier = Modifier.fillMaxSize()) {
        var tabIndex by rememberSaveable { mutableIntStateOf(0) }
        val tabs = listOf("Productos", "Categorías", "Marcas")

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
                ProductScreen(navController = navController)
            }
            1 -> {
                CategoryScreen(
                    navController = navController)
            }
            2 -> {
                BrandScreen(
                    navController = navController)
            }
        }
    }
}
