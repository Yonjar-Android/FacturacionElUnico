@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.facturacionelunico.presentation.sharedComponents

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun DialogFormCreateUpdate(
    title: String,  // Título personalizable ("Editar Marca", "Editar Categoría", etc.)
    textButton:String,
    value: String,
    onValueChange: (String) -> Unit,
    dismiss: () -> Unit,
    onConfirm: (newValue: String) -> Unit  // Función lambda para manejar la confirmación
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
            Text(title, fontWeight = FontWeight.Bold, fontSize = 24.sp)

            Column {
                Text("Nombre", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Spacer(modifier = Modifier.size(5.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = { onValueChange(it) },
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
                    onConfirm(value)  // Llama a la función proporcionada
                },
                colors = ButtonDefaults.buttonColors(containerColor = blueUi)
            ) {
                Text(
                    textButton,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}