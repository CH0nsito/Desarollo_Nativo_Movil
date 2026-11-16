package com.example.practica14

import org.osmdroid.tileprovider.tilesource.XYTileSource
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.practica14.data.AppDatabase
import com.example.practica14.data.MarcadorEntity
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

private fun tienePermiso(context: android.content.Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaOSMScreen() {
    val context = LocalContext.current
    val lifecycle = (context as ComponentActivity).lifecycle
    val scope = rememberCoroutineScope()
    val dao = remember { AppDatabase.getDatabase(context).marcadorDao() }

    // ---------- Permisos ----------
    var permiso by remember { mutableStateOf(tienePermiso(context)) }
    var denegado by remember { mutableStateOf(false) }
    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        permiso = resultado.values.any { it }
        denegado = !permiso
    }
    val pedirPermisos = {
        permisosLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    LaunchedEffect(Unit) { if (!permiso) pedirPermisos() }

    if (!permiso) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (denegado) "Permiso de ubicación denegado. La app lo necesita para mostrar tu posición en el mapa."
                else "Se necesita el permiso de ubicación.",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = { pedirPermisos() }) { Text("Conceder permiso") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            }) { Text("Abrir ajustes") }
        }
        return
    }

    // ---------- Mapa ----------
    val mapView = remember {
        MapView(context).apply {
            setTileSource(EsriCalles)
            setMultiTouchControls(true)
            controller.setZoom(16.0)
            controller.setCenter(GeoPoint(25.7903, -108.9859)) // Los Mochis (mientras llega el GPS)
        }
    }

    // RETO Fase 1: ícono rojo (Vector Asset -> Bitmap)
    val iconoRojo = remember {
        ContextCompat.getDrawable(context, R.drawable.ic_ubicacion_roja)!!.toBitmap(96, 96)
    }
    val miUbicacionOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            setPersonIcon(iconoRojo)
            setDirectionArrow(iconoRojo, iconoRojo)
            setPersonAnchor(0.5f, 1.0f)
            enableMyLocation()
        }
    }
    val marcadorYo = remember {
        Marker(mapView).apply {
            title = "Estoy aquí"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    // Overlays fijos (se agregan una sola vez)
    LaunchedEffect(Unit) {
        // Long Press -> guardar en Room
        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
            override fun longPressHelper(p: GeoPoint?): Boolean {
                p ?: return false
                val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                scope.launch {
                    dao.insertar(
                        MarcadorEntity(
                            latitud = p.latitude,
                            longitud = p.longitude,
                            titulo = "Marcador $hora"
                        )
                    )
                }
                Toast.makeText(context, "Marcador guardado", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        mapView.overlays.add(0, MapEventsOverlay(receiver))
        mapView.overlays.add(miUbicacionOverlay)
        mapView.overlays.add(marcadorYo)
    }

    // Ciclo de vida del mapa
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    miUbicacionOverlay.enableMyLocation()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                    miUbicacionOverlay.disableMyLocation()
                }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // ---------- GPS con FusedLocationProviderClient ----------
    var ubicacion by remember { mutableStateOf<GeoPoint?>(null) }
    var centrado by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(resultado: LocationResult) {
                resultado.lastLocation?.let { ubicacion = GeoPoint(it.latitude, it.longitude) }
            }
        }
        var activo = false
        val observer = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_START -> if (!activo) {
                    client.lastLocation.addOnSuccessListener { loc ->
                        loc?.let { ubicacion = GeoPoint(it.latitude, it.longitude) }
                    }
                    client.requestLocationUpdates(request, callback, Looper.getMainLooper())
                    activo = true
                }
                Lifecycle.Event.ON_STOP -> if (activo) {
                    client.removeLocationUpdates(callback)
                    activo = false
                }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            if (activo) client.removeLocationUpdates(callback)
        }
    }

    // Cada nueva coordenada: mueve el marcador y centra la cámara la primera vez
    LaunchedEffect(ubicacion) {
        ubicacion?.let {
            marcadorYo.position = it
            if (!centrado) {
                mapView.controller.setZoom(17.0)
                mapView.controller.animateTo(it)
                centrado = true
            }
            mapView.invalidate()
        }
    }

    // ---------- RETO Fase 2: marcadores desde Room (Flow) ----------
    val marcadores by dao.obtenerTodos().collectAsState(initial = emptyList())
    LaunchedEffect(marcadores) {
        mapView.overlays.removeAll { it is Marker && it.id == "db" }
        marcadores.forEach { m ->
            mapView.overlays.add(
                Marker(mapView).apply {
                    id = "db"
                    position = GeoPoint(m.latitud, m.longitud)
                    title = m.titulo
                    snippet = "%.5f, %.5f".format(m.latitud, m.longitud)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
            )
        }
        mapView.invalidate()
    }

    // ---------- UI ----------
    Box(Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("GPS (FusedLocationProviderClient)", fontWeight = FontWeight.Bold)
                val u = ubicacion
                Text(
                    if (u == null) "Buscando ubicación..."
                    else "Lat: %.6f   Lon: %.6f".format(u.latitude, u.longitude)
                )
                Text(
                    "Marcadores guardados: ${marcadores.size} (mantén presionado el mapa para agregar)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Column(
            Modifier.align(Alignment.BottomEnd).padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallFloatingActionButton(onClick = { scope.launch { dao.borrarTodos() } }) {
                Text("🗑")
            }
            FloatingActionButton(onClick = {
                ubicacion?.let { mapView.controller.animateTo(it) }
            }) {
                Icon(Icons.Default.LocationOn, contentDescription = "Centrar")
            }
        }
    }

}

private object EsriCalles : OnlineTileSourceBase(
    "EsriCalles", 0, 19, 256, "",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String =
        baseUrl +
                MapTileIndex.getZoom(pMapTileIndex) + "/" +
                MapTileIndex.getY(pMapTileIndex) + "/" +
                MapTileIndex.getX(pMapTileIndex)
}