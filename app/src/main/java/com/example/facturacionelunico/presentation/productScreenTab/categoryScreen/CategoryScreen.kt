@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.productScreenTab.categoryScreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.facturacionelunico.domain.models.CategoryDomainModel
import com.example.facturacionelunico.presentation.sharedComponents.AddButton
import com.example.facturacionelunico.presentation.sharedComponents.DialogFormCreateUpdate
import com.example.facturacionelunico.presentation.sharedComponents.SearchBarComponent
import com.example.facturacionelunico.ui.theme.blueUi
import com.example.facturacionelunico.utils.transform.FormatNames

@Composable
fun CategoryScreen(
    navController: NavController,
    categoryScreenViewModel: CategoryScreenViewModel = hiltViewModel()) {

    val contextHere = LocalContext.current

    val categories by categoryScreenViewModel.categories.collectAsStateWithLifecycle()

    val searchQuery by categoryScreenViewModel.searchQuery.collectAsStateWithLifecycle()

    val message by categoryScreenViewModel.message.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var textValueCategory by remember { mutableStateOf("") }

    println(message)

    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()){
            Toast.makeText(contextHere, message, Toast.LENGTH_SHORT).show()
            categoryScreenViewModel.restartMessage()

            if (!message!!.contains("Error")){
                showDialog = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, bottom = 20.dp)

        ) {
            SearchBarComponent(searchQuery, onChangeValue = { newQuery ->
                categoryScreenViewModel.updateQuery(newQuery)
            })

            Spacer(modifier = Modifier.size(10.dp))

            LazyColumn {
                items(categories) {
                    CategoryItem(it,
                        navigate = { id, name ->
                            navController.navigate("CategoryDetailScreen/$id")
                        })
                }
            }

            if (showDialog) {
                DialogFormCreateUpdate(
                    title = "Crear Categoría",
                    textButton = "Crear",
                    value = textValueCategory,
                    dismiss = {
                        textValueCategory = ""
                        showDialog = false
                    },
                    onValueChange = { textValueCategory = it },
                    onConfirm = { name ->
                        categoryScreenViewModel.createCategory(FormatNames.firstLetterUpperCase(name))
                    }
                )
            }
        }

        AddButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            functionClick = { showDialog = true })
    }

    // No realizar navegación hacia atrás desde esta pantalla
    BackHandler {}
}

@Composable
fun CategoryItem(category: CategoryDomainModel, navigate: (Long, String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.6f),
            text = category.categoryName, fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.weight(0.1f))

        Button(
            modifier = Modifier.weight(0.3f),
            colors = ButtonDefaults.buttonColors(
                containerColor = blueUi
            ),
            onClick = {
                navigate.invoke(category.categoryId,category.categoryName)
            }
        ) {
            Text(
                text = "Ver productos", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}
