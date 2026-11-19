package com.example.practica01

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FirstScreen(onNavigateToSecondScreen: (String) -> Unit) {
    var textoIngresado by remember { mutableStateOf("") }
    Scaffold(
        containerColor = Color(0xFFE3F2FD)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = textoIngresado,
                onValueChange = { textoIngresado = it },
                label = { Text("Escribe tu texto") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (textoIngresado.isNotBlank()) {
                        onNavigateToSecondScreen(textoIngresado)
                    }
                }
            ) {
                Text("Ir a SecondScreen")
            }
        }
    }
}