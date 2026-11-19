package com.example.practica04.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomRadioButton() {
    var selectedOption by remember { mutableStateOf("Opción 1") }

    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = (selectedOption == "Opción 1"),
            onClick = { selectedOption = "Opción 1" }
        )
        Text(
            text = "Opción 1",
            modifier = Modifier.clickable { selectedOption = "Opción 1" }
        )

        Spacer(modifier = Modifier.width(16.dp))

        RadioButton(
            selected = (selectedOption == "Opción 2"),
            onClick = { selectedOption = "Opción 2" }
        )
        Text(
            text = "Opción 2",
            modifier = Modifier.clickable { selectedOption = "Opción 2" }
        )
    }
}
