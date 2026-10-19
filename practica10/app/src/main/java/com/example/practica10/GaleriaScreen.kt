package com.example.practica10

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage

@Composable
fun GaleriaScreen() {
    // Lista de URLs de prueba (la última es inválida a propósito para ver el estado de error)
    val imagenes = listOf(
        "https://picsum.photos/id/1018/400/400",
        "https://picsum.photos/id/1015/400/400",
        "https://picsum.photos/id/1019/400/400",
        "https://picsum.photos/id/1020/400/400",
        "https://picsum.photos/id/1021/400/400",
        "https://picsum.photos/id/1022/400/400",
        "https://picsum.photos/id/1023/400/400",
        "https://picsum.photos/id/1024/400/400",
        "https://picsum.photos/id/1025/400/400",
        "https://picsum.photos/id/1026/400/400",
        "https://picsum.photos/id/1027/400/400",
        "https://picsum.photos/id/1028/400/400",
        "https://picsum.photos/id/1029/400/400",
        "https://picsum.photos/id/1011/400/400",
        "https://picsum.photos/id/1012/400/400",
        "https://picsum.photos/id/1013/400/400",
        "https://picsum.photos/id/1014/400/400",
        "https://esta-url-no-existe.invalid/imagen.jpg" // borra esta línea si no la quieres
    )

    // RETO: estado de la imagen seleccionada (null = sin diálogo)
    var seleccionada by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .padding(top = 32.dp)
    ) {
        Text(
            text = "Mi Galería",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

        // Cuadrícula adaptativa: se ajusta sola al ancho de la pantalla
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(imagenes) { url ->
                ImagenItem(url = url, onClick = { seleccionada = url })
            }
        }
    }

    // Vista de detalle
    seleccionada?.let { url ->
        Dialog(onDismissRequest = { seleccionada = null }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ImagenCoil(
                        url = url.replace("/400/400", "/900/900"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    TextButton(
                        onClick = { seleccionada = null },
                        modifier = Modifier.padding(4.dp)
                    ) { Text("Cerrar") }
                }
            }
        }
    }
}

@Composable
fun ImagenItem(url: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        ImagenCoil(url = url, modifier = Modifier.fillMaxSize())
    }
}

// AsyncImage de Coil con indicador de carga (placeholder) y manejo de errores
@Composable
fun ImagenCoil(url: String, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = "Imagen de la galería",
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        },
        error = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se pudo cargar", style = MaterialTheme.typography.labelMedium)
            }
        }
    )
}