package com.example.practica13

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Servicio nativo de sensores de Android
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                BurbujaScreen(sensorManager = sensorManager)
            }
        }
    }
}

