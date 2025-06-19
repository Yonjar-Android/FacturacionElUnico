package com.example.facturacionelunico.presentation.sharedComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun GenericBlueUiButton(
    buttonText: String,
    enabled: Boolean = true,
    onFunction: () -> Unit
){
    Button(
        modifier = Modifier.fillMaxWidth(fraction = 0.9f),
        onClick = {
            onFunction.invoke()
        }, colors = ButtonDefaults.buttonColors(containerColor = blueUi),
        enabled = enabled
    ) {
        Text(
            buttonText, fontWeight = FontWeight.Bold, fontSize = 22.sp,
            color = Color.White
        )
    }
}