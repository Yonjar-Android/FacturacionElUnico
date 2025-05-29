package com.example.facturacionelunico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.facturacionelunico.presentation.buyScreen.BuyScreenTab
import com.example.facturacionelunico.presentation.clientAndSupplierTab.ClientSupplierTab
import com.example.facturacionelunico.presentation.clientAndSupplierTab.clientScreen.ClientDetailScreen
import com.example.facturacionelunico.presentation.clientAndSupplierTab.supplierScreen.SupplierDetailScreen
import com.example.facturacionelunico.presentation.productScreenTab.ProductScreenTab
import com.example.facturacionelunico.presentation.productScreenTab.brandScreen.brandDetailScreen.BrandDetailScreen
import com.example.facturacionelunico.presentation.productScreenTab.categoryScreen.categoryDetailScreen.CategoryDetailScreen
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.ProductCreateScreen
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen.ProductDetailScreen
import com.example.facturacionelunico.presentation.productScreenTab.productScreen.productDetailScreen.ProductUpdateScreen
import com.example.facturacionelunico.presentation.sellScreen.SellScreenTab
import com.example.facturacionelunico.presentation.sellScreen.invoideDetailScreen.InvoiceDetailScreen
import com.example.facturacionelunico.ui.theme.FacturacionElUnicoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FacturacionElUnicoTheme {

                val context = LocalContext.current

                var selectedScreen by remember { mutableIntStateOf(0) }
                val controller = rememberNavController()

                Scaffold(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                    bottomBar = {
                        BottomNav(
                            selectedScreen = selectedScreen,
                            navController = controller,
                            onChangeScreen = { selectedScreen = it })
                    }
                ) {
                    NavHost(
                        navController = controller,
                        startDestination = "ProductScreen",
                        modifier = Modifier.padding(it)
                    ) {
                        /*Pantalla de productos*/
                        composable(route = "ProductScreen") {
                            ProductScreenTab(
                                navController = controller
                            )
                        }

                        /*Pantalla para crear productos*/
                        composable(route = "ProductCreateScreen") {
                            ProductCreateScreen(
                                navController = controller,
                                context = context
                            )
                        }

                        /*Pantalla de detalle de producto*/
                        composable(
                            route = "ProductDetailScreen/{productId}",
                            arguments = listOf(
                                navArgument("productId") { type = NavType.LongType }
                            )) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                            ProductDetailScreen(
                                navController = controller,
                                productId = productId
                            )
                        }

                        /*Pantalla para actualizar productos*/
                        composable(
                            route = "ProductUpdateScreen/{productId}",
                            arguments = listOf(
                                navArgument("productId") { type = NavType.LongType }
                            )) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L

                            ProductUpdateScreen(
                                productId = productId,
                                navController = controller,
                                context = context
                            )
                        }

                        /*Pantalla detalle de categoría*/
                        composable(
                            route = "CategoryDetailScreen/{categoryId}",
                            arguments = listOf(
                                navArgument("categoryId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                            CategoryDetailScreen(
                                categoryId = categoryId,
                                navController = controller
                            )
                        }

                        /*Pantalla detalle de marca*/
                        composable(
                            route = "BrandDetailScreen/{brandId}",
                            arguments = listOf(
                                navArgument("brandId") { type = NavType.LongType }
                            )) { backStackEntry ->
                            val brandId = backStackEntry.arguments?.getLong("brandId") ?: 0L
                            BrandDetailScreen(
                                brandId = brandId,
                                navController = controller
                            )
                        }

                        /*Pantalla de clientes y proveedores*/
                        composable(
                            route = "ClientSupplierScreen",
                        ) {
                            ClientSupplierTab(
                                navController = controller
                            )
                        }

                        /*Pantalla detalle de cliente*/
                        composable(
                            route = "ClientDetailScreen/{clientId}",
                            arguments = listOf(
                                navArgument("clientId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0

                            ClientDetailScreen(
                                clientId = clientId,
                                navController = controller
                            )
                        }

                        /*Pantalla detalle de proveedor*/
                        composable(
                            route = "SupplierDetailScreen/{supplierId}",
                            arguments = listOf(
                                navArgument("supplierId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val supplierId = backStackEntry.arguments?.getLong("supplierId") ?: 0L

                            SupplierDetailScreen(
                                supplierId = supplierId,
                                navController = controller
                            )
                        }

                        /*Pantalla de facturación*/
                        composable(route = "SellScreenTab") {
                            SellScreenTab(
                                navController = controller
                            )
                        }

                        /*Pantalla detalle de factura*/
                        composable(route = "InvoiceDetailScreen/{invoiceId}",
                            arguments = listOf(
                                navArgument("invoiceId") { type = NavType.LongType }
                            )) { backStackEntry ->
                            val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L

                            InvoiceDetailScreen(
                                invoiceId = invoiceId,
                                navController = controller
                            )
                        }

                        /*Pantalla de compras*/
                        composable(route = "BuyScreenTab") {
                            BuyScreenTab(
                                navController = controller
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BottomNav(
        selectedScreen: Int,
        navController: NavController,
        onChangeScreen: (Int) -> Unit
    ) {
        NavigationBar {

            NavigationBarItem(
                selected = selectedScreen == 0, onClick = {
                    onChangeScreen.invoke(0)
                    navController.navigate("ProductScreen")
                }, icon = {
                    NavIcon(
                        icon = R.drawable.helmet,
                        description = "Product icon interface"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Blue,
                    selectedIconColor = Color.White
                )
            )

            NavigationBarItem(
                selected = selectedScreen == 1, onClick = {
                    onChangeScreen.invoke(1)
                    navController.navigate("ClientSupplierScreen")
                }, icon = {
                    NavIcon(
                        icon = R.drawable.client,
                        description = "Client icon interface"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Blue,
                    selectedIconColor = Color.White
                )
            )

            NavigationBarItem(
                selected = selectedScreen == 2, onClick = {
                    onChangeScreen.invoke(2)
                    navController.navigate("SellScreenTab")
                }, icon = {
                    NavIcon(
                        icon = R.drawable.cart,
                        description = "Sell icon interface"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Blue,
                    selectedIconColor = Color.White
                )
            )

            NavigationBarItem(
                selected = selectedScreen == 3, onClick = {
                    onChangeScreen.invoke(3)
                    navController.navigate("BuyScreenTab")
                }, icon = {
                    NavIcon(
                        icon = R.drawable.shopping_bag,
                        description = "Buy icon interface"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Blue,
                    selectedIconColor = Color.White
                )
            )

            NavigationBarItem(
                selected = selectedScreen == 4, onClick = {
                    onChangeScreen.invoke(4)
                    navController.navigate("ProductScreen")
                }, icon = {
                    NavIcon(
                        icon = R.drawable.report,
                        description = "Reports icon interface"
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Blue,
                    selectedIconColor = Color.White
                )
            )
        }
    }

    @Composable
    fun NavIcon(icon: Int, description: String) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = Modifier.size(30.dp)
        )
    }
}

