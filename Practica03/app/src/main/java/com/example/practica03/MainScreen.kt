package com.example.practica03

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen() {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Práctica 03: Intents y Navegación",
            fontSize = 22.sp,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 1. INTENT EXPLÍCITO: Abre ProfileActivity enviando datos
        Button(
            onClick = {
                val intent = Intent(context, ProfileActivity::class.java).apply {
                    putExtra("EXTRA_NOMBRE", nombre)
                    putExtra("EXTRA_CORREO", correo)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Perfil (Intent Explícito)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. INTENT IMPLÍCITO: Solicita al sistema compartir texto
        OutlinedButton(
            onClick = {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, "Hola, mi nombre es $nombre y mi correo es $correo")
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(sendIntent, "Compartir datos usando:")
                context.startActivity(chooser)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir Datos (Intent Implícito)")
        }
    }
}