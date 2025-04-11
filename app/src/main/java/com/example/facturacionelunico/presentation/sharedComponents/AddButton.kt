package com.example.facturacionelunico.presentation.sharedComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.facturacionelunico.ui.theme.blueUi

@Composable
fun AddButton(modifier: Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        FloatingActionButton(
            modifier = Modifier.padding(end = 10.dp, bottom = 10.dp),
            shape = CircleShape,
            containerColor = blueUi,
            onClick = {

            }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add icon",
                tint = Color.White
            )
        }
    }

}