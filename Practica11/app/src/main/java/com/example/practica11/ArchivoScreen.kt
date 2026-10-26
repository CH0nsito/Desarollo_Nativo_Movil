package com.example.practica11

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

// Devuelve la carpeta de destino y una etiqueta para mostrar en pantalla.
// Interna: filesDir. Externa: la SDCard si hay una montada; si no, el almacenamiento externo del dispositivo.
private fun carpetaDestino(context: Context, externo: Boolean): Pair<File, String>? {
    if (!externo) return Pair(context.filesDir, "Memoria interna")

    val dirs = context.getExternalFilesDirs(null).filterNotNull()
    // dirs[0] = almacenamiento externo primario, dirs[1..] = tarjetas SD removibles
    val sd = dirs.drop(1).firstOrNull { Environment.getExternalStorageState(it) == Environment.MEDIA_MOUNTED }
    if (sd != null) return Pair(sd, "SDCard")

    val primario = dirs.firstOrNull { Environment.getExternalStorageState(it) == Environment.MEDIA_MOUNTED }
    return primario?.let { Pair(it, "Almacenamiento externo (no se detectó SDCard)") }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("nota.txt") }
    var contenido by remember { mutableStateOf("") }
    var externo by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var leido by remember { mutableStateOf("") }

    fun concedido(p: String) =
        ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED

    var leerOk by remember { mutableStateOf(concedido(Manifest.permission.READ_EXTERNAL_STORAGE)) }
    var escribirOk by remember { mutableStateOf(concedido(Manifest.permission.WRITE_EXTERNAL_STORAGE)) }

    // Permisos en tiempo de ejecución
    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        leerOk = resultado[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        escribirOk = resultado[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
        mensaje = if (leerOk && escribirOk) "Permisos concedidos"
        else "Permisos denegados o no aplican en esta versión de Android"
    }

    // Arma el File según el destino elegido
    fun obtenerArchivo(): Pair<File, String>? {
        val n = nombre.trim().substringAfterLast('/')
        if (n.isBlank()) { mensaje = "Escribe un nombre de archivo"; return null }
        val destino = carpetaDestino(context, externo)
        if (destino == null) { mensaje = "Almacenamiento externo no disponible"; return null }
        return Pair(File(destino.first, n), destino.second)
    }

    fun guardar(agregar: Boolean) {
        val (archivo, lugar) = obtenerArchivo() ?: return
        scope.launch {
            val r = withContext(Dispatchers.IO) {
                runCatching {
                    // append = true agrega al final; false sobrescribe
                    FileOutputStream(archivo, agregar).use { out ->
                        out.write((if (agregar) contenido + "\n" else contenido).toByteArray())
                    }
                }
            }
            mensaje = if (r.isSuccess)
                (if (agregar) "Texto agregado" else "Archivo guardado") + " en $lugar:\n${archivo.absolutePath}"
            else "Error al guardar: ${r.exceptionOrNull()?.message}"
        }
    }

    fun leer() {
        val (archivo, lugar) = obtenerArchivo() ?: return
        scope.launch {
            val r = withContext(Dispatchers.IO) {
                runCatching {
                    if (!archivo.exists()) null
                    else FileInputStream(archivo).bufferedReader().use { it.readText() }
                }
            }
            if (r.isSuccess) {
                val texto = r.getOrNull()
                if (texto == null) { leido = ""; mensaje = "El archivo no existe en $lugar" }
                else { leido = texto; mensaje = "Archivo leído desde $lugar:\n${archivo.absolutePath}" }
            } else mensaje = "Error al leer: ${r.exceptionOrNull()?.message}"
        }
    }

    fun borrar() {
        val (archivo, lugar) = obtenerArchivo() ?: return
        mensaje = if (archivo.exists() && archivo.delete()) { leido = ""; "Archivo eliminado de $lugar" }
        else "No hay archivo para eliminar en $lugar"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Archivos y SDCard") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Permisos
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Permisos de almacenamiento", style = MaterialTheme.typography.titleMedium)
                    Text("READ_EXTERNAL_STORAGE: " + if (leerOk) "concedido" else "no concedido")
                    Text("WRITE_EXTERNAL_STORAGE: " + if (escribirOk) "concedido" else "no concedido")
                    OutlinedButton(onClick = {
                        permisosLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    }) { Text("Solicitar permisos") }
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del archivo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Texto a guardar") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Selección del destino
            Text("Ubicación de destino", style = MaterialTheme.typography.titleMedium)
            listOf(false to "Memoria interna", true to "SDCard / almacenamiento externo").forEach { (valor, texto) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(selected = externo == valor, onClick = { externo = valor }, role = Role.RadioButton),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = externo == valor, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(texto)
                }
            }

            // Acciones
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { guardar(false) }, modifier = Modifier.weight(1f)) { Text("Guardar") }
                Button(onClick = { guardar(true) }, modifier = Modifier.weight(1f)) { Text("Agregar") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { leer() }, modifier = Modifier.weight(1f)) { Text("Leer") }
                OutlinedButton(onClick = { borrar() }, modifier = Modifier.weight(1f)) { Text("Eliminar") }
            }

            if (mensaje.isNotEmpty()) {
                Card(Modifier.fillMaxWidth()) {
                    Text(mensaje, Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                }
            }

            if (leido.isNotEmpty()) {
                Text("Contenido leído", style = MaterialTheme.typography.titleMedium)
                Card(Modifier.fillMaxWidth()) {
                    Text(leido, Modifier.padding(12.dp))
                }
            }
        }
    }
}