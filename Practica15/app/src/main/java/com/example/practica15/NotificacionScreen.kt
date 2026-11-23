package com.example.practica15

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionScreen() {
    val context = LocalContext.current
    val portapapeles = LocalClipboardManager.current
    var token by remember { mutableStateOf("Obteniendo token...") }
    var permisoConcedido by remember { mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
        if (!concedido) {
            Toast.makeText(context, "Sin permiso no habrá notificaciones", Toast.LENGTH_LONG).show()
        }
    }

    // Pide el permiso (Android 13+) y obtiene el FCM Token al abrir la pantalla
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { tarea ->
            if (tarea.isSuccessful) {
                token = tarea.result
                Log.d("FCM_TOKEN", tarea.result)
            } else {
                token = "Error al obtener el token: ${tarea.exception?.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Práctica 15 - Notificaciones") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Fase A: Notificación local", style = MaterialTheme.typography.titleMedium)
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (!permisoConcedido && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        mostrarNotificacion(
                            context,
                            "Notificación local",
                            "Tocaste el botón. Toca esta alerta para abrir la pantalla de detalle.",
                            "detalle"
                        )
                    }
                }
            ) { Text("Disparar notificación local") }

            HorizontalDivider()

            Text("Fase B: Firebase Cloud Messaging", style = MaterialTheme.typography.titleMedium)
            Text("FCM Token del dispositivo:", style = MaterialTheme.typography.labelLarge)
            Card(Modifier.fillMaxWidth()) {
                SelectionContainer { Text(token, modifier = Modifier.padding(12.dp)) }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    portapapeles.setText(AnnotatedString(token))
                    Toast.makeText(context, "Token copiado", Toast.LENGTH_SHORT).show()
                }
            ) { Text("Copiar token") }
        }
    }
}

@Composable
private fun SelectionContainer(content: @Composable () -> Unit) =
    androidx.compose.foundation.text.selection.SelectionContainer(content = content)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(destino: String, mensaje: String, onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pantalla de destino") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("¡Llegaste por una notificación!", style = MaterialTheme.typography.headlineSmall)
            Text("Destino (Data Payload): $destino")
            Text("Mensaje: $mensaje")
            Button(onClick = onVolver) { Text("Volver al inicio") }
        }
    }
}