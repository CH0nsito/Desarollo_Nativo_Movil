package com.example.practica13

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val GRAVEDAD = 9.81f   // m/s² en reposo
private const val UMBRAL_G = 2.0f    // umbral de sacudida (en múltiplos de g)
private const val ESPERA_MS = 800L   // evita contar la misma sacudida varias veces

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurbujaScreen(sensorManager: SensorManager) {
    val context = LocalContext.current

    var x by remember { mutableFloatStateOf(0f) }
    var y by remember { mutableFloatStateOf(0f) }
    var z by remember { mutableFloatStateOf(0f) }
    var fuerzaG by remember { mutableFloatStateOf(1f) }
    var sacudidas by remember { mutableIntStateOf(0) }
    var sacudiendo by remember { mutableStateOf(false) }

    val acelerometro = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    // El sensor se enciende al abrir/volver a la pantalla y se apaga al pausar (ahorra batería)
    DisposableEffect(Unit) {
        var ultimaSacudida = 0L

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                x = event.values[0]
                y = event.values[1]
                z = event.values[2]

                // Aceleración vectorial: magnitud del vector (x, y, z) expresada en g
                val fuerza = sqrt(x * x + y * y + z * z) / GRAVEDAD
                fuerzaG = fuerza

                val ahora = System.currentTimeMillis()
                if (fuerza > UMBRAL_G && ahora - ultimaSacudida > ESPERA_MS) {
                    ultimaSacudida = ahora
                    sacudidas++
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val lifecycle = (context as ComponentActivity).lifecycle
        val observer = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> if (acelerometro != null) {
                    sensorManager.registerListener(
                        listener, acelerometro, SensorManager.SENSOR_DELAY_GAME
                    )
                }
                Lifecycle.Event.ON_PAUSE -> sensorManager.unregisterListener(listener)
                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            sensorManager.unregisterListener(listener)
        }
    }

    // Respuesta visual: el mensaje y el fondo cambian durante 1 segundo
    LaunchedEffect(sacudidas) {
        if (sacudidas > 0) {
            sacudiendo = true
            delay(1000)
            sacudiendo = false
        }
    }
    val fondo by animateColorAsState(
        targetValue = if (sacudiendo) Color(0xFFD32F2F) else Color.DarkGray,
        label = "fondo"
    )

    // Burbuja: se mueve según X e Y y se limita al borde del círculo
    val density = LocalDensity.current
    val sensibilidadPx = with(density) { 12.dp.toPx() }
    val radioMaxPx = with(density) { 101.dp.toPx() } // 125 (radio) - 4 (borde) - 20 (radio burbuja)
    val rawX = -x * sensibilidadPx
    val rawY = y * sensibilidadPx
    val distancia = sqrt(rawX * rawX + rawY * rawY)
    val offsetX: Float
    val offsetY: Float
    if (distancia > radioMaxPx) {
        val proporcion = radioMaxPx / distancia
        offsetX = rawX * proporcion
        offsetY = rawY * proporcion
    } else {
        offsetX = rawX
        offsetY = rawY
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acelerómetro y Sacudidas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(fondo)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Valores en tiempo real
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (acelerometro == null) {
                        Text("Este dispositivo no tiene acelerómetro", color = Color.White)
                    }
                    Text("Eje X: ${"%.2f".format(x)} m/s²", color = Color.White, fontSize = 20.sp)
                    Text("Eje Y: ${"%.2f".format(y)} m/s²", color = Color.White, fontSize = 20.sp)
                    Text("Eje Z: ${"%.2f".format(z)} m/s²", color = Color.White, fontSize = 20.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Fuerza: ${"%.2f".format(fuerzaG)} g (umbral $UMBRAL_G g)",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }

                // Nivel de burbuja
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .border(4.dp, Color.White, CircleShape)
                        .background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(50.dp).border(2.dp, Color.Red, CircleShape))
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .size(40.dp)
                            .background(Color(0xFF00FF00), CircleShape)
                    )
                }

                // Estado de la sacudida
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (sacudiendo) "¡SACUDIDA DETECTADA!" else "Sacude el dispositivo",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Sacudidas: $sacudidas", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}