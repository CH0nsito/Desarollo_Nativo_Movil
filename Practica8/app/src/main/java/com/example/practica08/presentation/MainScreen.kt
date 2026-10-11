package com.example.practica08.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.practica08.data.Usuario
import com.example.practica08.domain.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MainScreen() {
    val context = LocalContext.current
    // Obtenemos la instancia de la BD y el DAO
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.usuarioDao()

    // Variable para ejecutar las corrutinas (inserciones y borrados)
    val coroutineScope = rememberCoroutineScope()

    // collectAsState() transforma el Flow en un Estado que redibuja la UI si hay cambios
    val usuarios by dao.obtenerTodos().collectAsState(initial = emptyList())

    var nuevoNombre by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp)
    ) {
        OutlinedTextField(
            value = nuevoNombre,
            onValueChange = { nuevoNombre = it },
            label = { Text("Nombre del usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (nuevoNombre.isNotBlank()) {
                    coroutineScope.launch(Dispatchers.IO) {
                        // 1. Guardar en la base de datos (hilo secundario)
                        dao.insertar(Usuario(nombre = nuevoNombre))

                        // 2. Regresar al hilo principal para actualizar la UI
                        withContext(Dispatchers.Main) {
                            nuevoNombre = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Usuario")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Lista de Usuarios Registrados:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(usuarios, key = { it.id }) { usuario ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    // Reto: Row con el texto y el ícono de papelera
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "ID: ${usuario.id} | Nombre: ${usuario.nombre}")

                        IconButton(onClick = {
                            coroutineScope.launch {
                                dao.eliminar(usuario)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                }
            }
        }
    }
}