package com.example.registroestudiantes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    matricula: String,
    nombre: String,
    carrera: String,
    turno: String,
    estatus: Boolean,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmación de Registro") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Matrícula:", style = MaterialTheme.typography.labelLarge)
                    Text(matricula, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Nombre:", style = MaterialTheme.typography.labelLarge)
                    Text(nombre, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Carrera:", style = MaterialTheme.typography.labelLarge)
                    Text(carrera, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Turno:", style = MaterialTheme.typography.labelLarge)
                    Text(turno, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Estatus:", style = MaterialTheme.typography.labelLarge)
                    Text(if (estatus) "Activo" else "Inactivo", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Registro")
            }
        }
    }
}