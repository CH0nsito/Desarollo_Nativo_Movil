package com.example.miniproyecto03.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.miniproyecto03.data.Publicacion
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatearFecha(ms: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "MX")).format(Date(ms))

@Composable
fun RedSocialApp(viewModel: PublicacionViewModel = viewModel()) {
    var mostrarForm by rememberSaveable { mutableStateOf(false) }
    val publicaciones by viewModel.publicaciones.collectAsState()

    if (mostrarForm) {
        BackHandler { mostrarForm = false }
        FormularioScreen(viewModel = viewModel, onTerminar = { mostrarForm = false })
    } else {
        ListaScreen(
            publicaciones = publicaciones,
            onNueva = { mostrarForm = true },
            onEliminar = { viewModel.eliminar(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    publicaciones: List<Publicacion>,
    onNueva: () -> Unit,
    onEliminar: (Publicacion) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Red Social Local") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNueva) {
                Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
            }
        }
    ) { padding ->
        if (publicaciones.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no hay publicaciones.\nToca + para crear la primera.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(publicaciones, key = { it.id }) { p ->
                    PublicacionItem(p, onEliminar = { onEliminar(p) })
                }
            }
        }
    }
}

@Composable
fun PublicacionItem(p: Publicacion, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            AsyncImage(
                model = p.fotoUri,
                contentDescription = p.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(p.titulo, style = MaterialTheme.typography.titleMedium)
                    if (p.descripcion.isNotBlank()) {
                        Text(p.descripcion, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatearFecha(p.fecha),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar publicación",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(viewModel: PublicacionViewModel, onTerminar: () -> Unit) {
    val context = LocalContext.current

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var archivoTemp by rememberSaveable { mutableStateOf<String?>(null) }
    val fecha = remember { System.currentTimeMillis() } // fecha generada automáticamente

    // Cámara: guarda la foto en el archivo que le pasamos
    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val ruta = archivoTemp
        if (ok && ruta != null) {
            fotoUri = Uri.fromFile(File(ruta)).toString()
        } else {
            Toast.makeText(context, "Captura cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    fun abrirCamara() {
        val archivo = viewModel.nuevoArchivo()
        archivoTemp = archivo.absolutePath
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        camaraLauncher.launch(uri)
    }

    // Permiso de cámara en tiempo de ejecución
    val permisoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) abrirCamara()
        else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    // Galería: selector de fotos del sistema (no requiere permiso)
    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.copiarDeGaleria(uri) { fotoUri = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva publicación") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = { TextButton(onClick = onTerminar) { Text("Cancelar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Fecha: ${formatearFecha(fecha)}", style = MaterialTheme.typography.bodyLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val tienePermiso = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (tienePermiso) abrirCamara()
                        else permisoLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Tomar foto") }

                OutlinedButton(
                    onClick = {
                        galeriaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Galería") }
            }

            if (fotoUri != null) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Vista previa",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(240.dp)
                )
            } else {
                Text("Aún no has agregado una foto", style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    viewModel.guardar(titulo.trim(), descripcion.trim(), fotoUri!!, fecha)
                    onTerminar()
                },
                enabled = titulo.isNotBlank() && fotoUri != null,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Publicar") }
        }
    }
}