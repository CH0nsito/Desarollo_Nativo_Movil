package com.example.practica12.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Mapea los tipos de Pokémon a colores de cartas TCG
fun obtenerColorPorTipo(tipo: String): Color {
    return when (tipo.lowercase()) {
        "grass", "bug" -> Color(0xFF78C850)
        "fire" -> Color(0xFFF08030)
        "water", "ice" -> Color(0xFF6890F0)
        "electric" -> Color(0xFFFFCE4B)
        "psychic", "poison", "ghost" -> Color(0xFFA040A0)
        "fighting", "rock", "ground" -> Color(0xFFC03028)
        "fairy" -> Color(0xFFEE99AC)
        "dark" -> Color(0xFF705848)
        "steel" -> Color(0xFFB8B8D0)
        "dragon" -> Color(0xFF7038F8)
        else -> Color(0xFFA8A878)
    }
}

@Composable
fun PokemonDetailScreen(viewModel: PokeViewModel, pokeId: String) {
    LaunchedEffect(pokeId) { viewModel.cargarDetallePokemon(pokeId) }

    val pokemon = viewModel.pokemonDetalle.value

    if (pokemon == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Tipo principal del Pokémon (el primero de la lista)
        val tipoPrincipal = pokemon.types.firstOrNull()?.type?.name ?: "normal"
        val colorCarta = obtenerColorPorTipo(tipoPrincipal)

        // Fondo de la pantalla
        Box(
            modifier = Modifier.fillMaxSize().background(Color.DarkGray).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Carta TCG dinámica
            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(0.7f),
                colors = CardDefaults.cardColors(containerColor = colorCarta),
                elevation = CardDefaults.cardElevation(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Encabezado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            pokemon.name.uppercase(), fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold, color = Color.Black
                        )
                        val hp = pokemon.stats.find { it.stat.name == "hp" }?.base_stat ?: 0
                        Text("HP $hp", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Imagen oficial
                    val imageUrl =
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                            .border(6.dp, Color(0xFFC0C0C0), RoundedCornerShape(4.dp)), // borde plateado
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = pokemon.name,
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 2.dp, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Estadísticas básicas
                    Text("ESTADÍSTICAS (Ataques)", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    pokemon.stats.filter { it.stat.name != "hp" }.forEach { stat ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stat.stat.name.uppercase(), color = Color.DarkGray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                stat.base_stat.toString(), fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}