package com.example.practica10

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamaraScreen() {
    val context = LocalContext.current

    // Lista observable de imágenes (Bitmaps)
    var listaImagenes by remember { mutableStateOf(listOf<Bitmap>()) }

    // Estado del permiso de cámara
    var permisoConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador de la cámara: devuelve el Bitmap capturado
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            listaImagenes = listaImagenes + bitmap
        }
    }

    // RETO: lanzador para pedir el permiso de cámara en tiempo de ejecución
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
        Toast.makeText(
            context,
            if (concedido) "Permiso de cámara concedido" else "Permiso de cámara denegado",
            Toast.LENGTH_SHORT
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cámara y Galería") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // RETO: verificar el permiso antes de abrir la cámara
                    if (permisoConcedido) {
                        camaraLauncher.launch(null)
                    } else {
                        permisoLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tomar Foto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            // Botones de acción: solicitar permiso y activar cámara
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { permisoLauncher.launch(Manifest.permission.CAMERA) },
                    enabled = !permisoConcedido,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (permisoConcedido) "Permiso concedido" else "Solicitar permiso")
                }
                Button(
                    onClick = {
                        if (permisoConcedido) camaraLauncher.launch(null)
                        else permisoLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Abrir cámara")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (listaImagenes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay fotos en la galería. ¡Toma una!")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaImagenes) { bitmap ->
                        ImagenCapturadaItem(bitmap = bitmap)
                    }
                }
            }
        }
    }
}

@Composable
fun ImagenCapturadaItem(bitmap: Bitmap) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        // Convertimos el Bitmap a ImageBitmap de Compose
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Foto capturada",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}