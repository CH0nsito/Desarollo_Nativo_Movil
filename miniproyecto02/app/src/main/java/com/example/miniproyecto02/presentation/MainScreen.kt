package com.example.miniproyecto02.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.miniproyecto02.data.Tarea
import com.example.miniproyecto02.domain.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Filtro(val etiqueta: String) {
    TODAS("Todas"), PENDIENTES("Pendientes"), COMPLETADAS("Completadas")
}

enum class Orden(val etiqueta: String) {
    FECHA_DESC("Más recientes primero"),
    FECHA_ASC("Más antiguas primero"),
    ESTADO("Pendientes primero")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).tareaDao() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val tareas by remember { dao.obtenerTodas() }.collectAsState(initial = emptyList())

    var filtro by rememberSaveable { mutableStateOf(Filtro.TODAS) }
    var orden by rememberSaveable { mutableStateOf(Orden.FECHA_DESC) }
    var menuAbierto by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var tareaEditando by remember { mutableStateOf<Tarea?>(null) }

    val visibles = remember(tareas, filtro, orden) {
        val filtradas = when (filtro) {
            Filtro.TODAS -> tareas
            Filtro.PENDIENTES -> tareas.filter { !it.completada }
            Filtro.COMPLETADAS -> tareas.filter { it.completada }
        }
        when (orden) {
            Orden.FECHA_DESC -> filtradas.sortedByDescending { it.fechaCreacion }
            Orden.FECHA_ASC -> filtradas.sortedBy { it.fechaCreacion }
            Orden.ESTADO -> filtradas.sortedWith(
                compareBy<Tarea> { it.completada }.thenByDescending { it.fechaCreacion }
            )
        }
    }

    val pendientes = tareas.count { !it.completada }
    val hayCompletadas = tareas.any { it.completada }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas ($pendientes pendientes)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            if (hayCompletadas) {
                                dao.eliminarCompletadas()
                                snackbarHostState.showSnackbar("Tareas completadas eliminadas")
                            } else {
                                snackbarHostState.showSnackbar("No hay tareas completadas")
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar completadas")
                    }

                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("FILTRAR", style = MaterialTheme.typography.labelSmall) },
                                onClick = {},
                                enabled = false
                            )
                            Filtro.entries.forEach { f ->
                                DropdownMenuItem(
                                    text = { Text((if (f == filtro) "✓ " else "    ") + f.etiqueta) },
                                    onClick = { filtro = f; menuAbierto = false }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("ORDENAR", style = MaterialTheme.typography.labelSmall) },
                                onClick = {},
                                enabled = false
                            )
                            Orden.entries.forEach { o ->
                                DropdownMenuItem(
                                    text = { Text((if (o == orden) "✓ " else "    ") + o.etiqueta) },
                                    onClick = { orden = o; menuAbierto = false }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                tareaEditando = null
                mostrarDialogo = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (visibles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (tareas.isEmpty()) "Aún no tienes tareas.\nToca + para agregar una."
                    else "No hay tareas con este filtro.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibles, key = { it.id }) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onToggle = { marcada ->
                            scope.launch { dao.actualizar(tarea.copy(completada = marcada)) }
                        },
                        onEditar = {
                            tareaEditando = tarea
                            mostrarDialogo = true
                        },
                        onEliminar = {
                            scope.launch {
                                dao.eliminar(tarea)
                                val r = snackbarHostState.showSnackbar(
                                    message = "Tarea eliminada",
                                    actionLabel = "Deshacer"
                                )
                                if (r == SnackbarResult.ActionPerformed) dao.insertar(tarea)
                            }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        TareaDialog(
            tarea = tareaEditando,
            onDismiss = { mostrarDialogo = false },
            onGuardar = { titulo, descripcion ->
                val editando = tareaEditando
                scope.launch {
                    if (editando == null) {
                        dao.insertar(Tarea(titulo = titulo, descripcion = descripcion))
                    } else {
                        dao.actualizar(editando.copy(titulo = titulo, descripcion = descripcion))
                    }
                }
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun TareaItem(
    tarea: Tarea,
    onToggle: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "MX")) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = tarea.completada, onCheckedChange = onToggle)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formato.format(Date(tarea.fechaCreacion)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar tarea")
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TareaDialog(
    tarea: Tarea?,
    onDismiss: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by remember { mutableStateOf(tarea?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(tarea?.descripcion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tarea == null) "Nueva tarea" else "Editar tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(titulo.trim(), descripcion.trim()) },
                enabled = titulo.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}