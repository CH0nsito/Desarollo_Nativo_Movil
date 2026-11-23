package com.example.practica15

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    private var destino by mutableStateOf<String?>(null)
    private var mensaje by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearCanalNotificaciones(this)
        procesarIntent(intent)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val d = destino
                if (d != null) {
                    DetalleScreen(d, mensaje) { destino = null }
                } else {
                    NotificacionScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarIntent(intent)
    }

    // Lee el Data Payload (extras), o el Deep Link practica://detalle?mensaje=...
    private fun procesarIntent(intent: Intent?) {
        intent ?: return
        val uri = intent.data
        val destinoExtra = intent.getStringExtra("destino")
        if (uri != null && uri.scheme == "practica15") {
            destino = uri.host
            mensaje = uri.getQueryParameter("mensaje") ?: "Abierto desde un Deep Link"
        } else if (destinoExtra != null) {
            destino = destinoExtra
            mensaje = intent.getStringExtra("mensaje") ?: "Sin mensaje"
        }
    }
}