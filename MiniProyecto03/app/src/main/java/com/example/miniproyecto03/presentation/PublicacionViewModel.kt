package com.example.miniproyecto03.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto03.data.AppDatabase
import com.example.miniproyecto03.data.Publicacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PublicacionViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).publicacionDao()

    val publicaciones: StateFlow<List<Publicacion>> = dao.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Archivo nuevo dentro de filesDir/fotos (privado de la app)
    fun nuevoArchivo(): File {
        val dir = File(getApplication<Application>().filesDir, "fotos").apply { mkdirs() }
        return File(dir, "IMG_${System.currentTimeMillis()}.jpg")
    }

    // Copia la imagen elegida en la galería a la app para que no se pierda el acceso
    fun copiarDeGaleria(uri: Uri, onListo: (String) -> Unit) {
        viewModelScope.launch {
            val ruta = withContext(Dispatchers.IO) {
                val destino = nuevoArchivo()
                getApplication<Application>().contentResolver.openInputStream(uri)?.use { input ->
                    destino.outputStream().use { out -> input.copyTo(out) }
                }
                Uri.fromFile(destino).toString()
            }
            onListo(ruta)
        }
    }

    fun guardar(titulo: String, descripcion: String, fotoUri: String, fecha: Long) {
        viewModelScope.launch {
            dao.insertar(Publicacion(titulo = titulo, descripcion = descripcion, fotoUri = fotoUri, fecha = fecha))
        }
    }

    fun eliminar(publicacion: Publicacion) {
        viewModelScope.launch {
            dao.eliminar(publicacion)
            withContext(Dispatchers.IO) {
                Uri.parse(publicacion.fotoUri).path?.let { File(it).delete() }
            }
        }
    }
}