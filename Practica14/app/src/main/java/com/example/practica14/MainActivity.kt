package com.example.practica14

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // osmdroid necesita un user agent y un lugar para la caché de tiles
        Configuration.getInstance().apply {
            load(this@MainActivity, getSharedPreferences("osm", MODE_PRIVATE))
            userAgentValue = "Practica14-DesarrolloMovil-UAS/1.0"
        }

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface { MapaOSMScreen() }
            }
        }
    }
}