package com.example.practica07

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current

    // Estado para controlar la visibilidad del menú desplegable
    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Aplicación") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Clic en Navegación", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    // Ícono de acción directa en la barra
                    IconButton(onClick = {
                        Toast.makeText(context, "Añadido a Favoritos", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorito")
                    }

                    // Ícono de más opciones (Overflow)
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }

                    // Menú desplegable
                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                menuExpandido = false
                                Toast.makeText(context, "Configuración seleccionada", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Acerca de") },
                            onClick = {
                                menuExpandido = false
                                Toast.makeText(context, "Acerca de seleccionada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )
        },
        // --- Reto: FloatingActionButton ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Elemento agregado", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { innerPadding ->
        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido a Material Design",
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Explora las opciones de la barra superior.",
                fontSize = 16.sp
            )
        }
    }
}