package com.example.practica09.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.practica09.data.Pokemon
import com.example.practica09.data.PokeClient
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Resultado(
    val metodo: String,
    val url: String,
    val codigo: Int,
    val contentType: String,
    val cuerpo: String,
    val pokemon: Pokemon?
)

private fun formatear(cuerpo: String): String {
    val texto = try {
        GsonBuilder().setPrettyPrinting().create()
            .toJson(JsonParser.parseString(cuerpo))
    } catch (e: Exception) {
        cuerpo
    }
    return if (texto.length > 4000) texto.take(4000) + "\n... (recortado)" else texto
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("pikachu") }
    var resultado by remember { mutableStateOf<Resultado?>(null) }
    var cargando by remember { mutableStateOf(false) }

    fun ejecutar(metodo: String) {
        val n = nombre.trim().lowercase()
        val url = "https://pokeapi.co/api/v2/pokemon/$n"
        scope.launch {
            cargando = true
            resultado = withContext(Dispatchers.IO) {
                try {
                    val resp = if (metodo == "GET") PokeClient.api.obtener(n)
                    else PokeClient.api.borrar(n)
                    val cuerpo = (if (resp.isSuccessful) resp.body()?.string()
                    else resp.errorBody()?.string()) ?: ""
                    val pokemon = if (resp.isSuccessful && metodo == "GET")
                        com.google.gson.Gson().fromJson(cuerpo, Pokemon::class.java)
                    else null
                    Resultado(
                        metodo, url, resp.code(),
                        resp.headers()["Content-Type"] ?: "—",
                        formatear(cuerpo), pokemon
                    )
                } catch (e: Exception) {
                    Resultado(metodo, url, -1, "—", "Error de red: ${e.message}", null)
                }
            }
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Analizador PokéAPI") })
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
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre o ID del Pokémon") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { ejecutar("GET") }, modifier = Modifier.weight(1f)) {
                    Text("GET")
                }
                OutlinedButton(onClick = { ejecutar("DELETE") }, modifier = Modifier.weight(1f)) {
                    Text("DELETE")
                }
            }

            if (cargando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            resultado?.let { r ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Petición", style = MaterialTheme.typography.titleMedium)
                        Text("${r.metodo} ${r.url}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Text(
                            "Código de estado: ${r.codigo}",
                            color = if (r.codigo in 200..299) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Content-Type: ${r.contentType}")
                    }
                }

                r.pokemon?.let { p ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AsyncImage(
                                model = p.sprites.other?.officialArtwork?.frontDefault
                                    ?: p.sprites.frontDefault,
                                contentDescription = p.name,
                                modifier = Modifier.size(180.dp)
                            )
                            Text(
                                "#${p.id} ${p.name.replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text("Tipos: ${p.types.joinToString { it.type.name }}")
                            Text("Altura: ${p.height} | Peso: ${p.weight} | Exp. base: ${p.baseExperience}")
                            HorizontalDivider(Modifier.padding(vertical = 4.dp))
                            p.stats.forEach { Text("${it.stat.name}: ${it.baseStat}") }
                        }
                    }
                }

                Text("JSON de la respuesta", style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = r.cuerpo,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}