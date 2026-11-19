package com.example.registroestudiantes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registroestudiantes.data.PreferencesManager

private val carreras = listOf(
    "Ingeniería de Software",
    "Ingeniería Industrial y de Sistemas",
    "Contaduría Pública",
    "Ingeniería Civil"
)

@Composable
fun RegistrationScreen(
    onNavigateToConfirmation: (
        matricula: String,
        nombre: String,
        carrera: String,
        turno: String,
        estatus: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    var matricula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf(carreras.first()) }
    var turno by remember { mutableStateOf("Matutino") }
    var estatusActivo by remember { mutableStateOf(true) }
    var carreraExpanded by remember { mutableStateOf(false) }

    // Carga la última matrícula/configuración guardada al abrir la pantalla
    LaunchedEffect(Unit) {
        if (preferencesManager.hasSavedData()) {
            matricula = preferencesManager.getMatricula()
            nombre = preferencesManager.getNombre()
            carrera = preferencesManager.getCarrera().ifEmpty { carreras.first() }
            turno = preferencesManager.getTurno()
            estatusActivo = preferencesManager.getEstatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Sistema de Registro de Estudiantes",
            fontSize = 22.sp,
            style = MaterialTheme.typography.headlineSmall
        )

        HorizontalDivider()

        // Matrícula
        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it },
            label = { Text("Matrícula") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Nombre completo
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Carrera - Exposed Dropdown Menu
        Text("Carrera", style = MaterialTheme.typography.titleMedium)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = carrera,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { carreraExpanded = true }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = carreraExpanded,
                onDismissRequest = { carreraExpanded = false }
            ) {
                carreras.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            carrera = opcion
                            carreraExpanded = false
                        }
                    )
                }
            }
        }

        // Turno - RadioButton
        Text("Turno", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = (turno == "Matutino"),
                onClick = { turno = "Matutino" }
            )
            Text(
                text = "Matutino",
                modifier = Modifier.clickable { turno = "Matutino" }
            )

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = (turno == "Vespertino"),
                onClick = { turno = "Vespertino" }
            )
            Text(
                text = "Vespertino",
                modifier = Modifier.clickable { turno = "Vespertino" }
            )
        }

        // Estatus - Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (estatusActivo) "Estatus: Activo" else "Estatus: Inactivo",
                fontSize = 16.sp
            )
            Switch(
                checked = estatusActivo,
                onCheckedChange = { estatusActivo = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de registro: guarda en SharedPreferences y navega con los datos
        Button(
            onClick = {
                if (matricula.isNotBlank() && nombre.isNotBlank()) {
                    preferencesManager.saveRegistration(
                        matricula = matricula,
                        nombre = nombre,
                        carrera = carrera,
                        turno = turno,
                        estatus = estatusActivo
                    )
                    onNavigateToConfirmation(matricula, nombre, carrera, turno, estatusActivo)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Estudiante")
        }
    }
}