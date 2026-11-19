package com.example.practica12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practica12.presentation.*

class MainActivity : ComponentActivity() {
    private val viewModel: PokeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                // systemBarsPadding evita que el contenido quede debajo de la barra de estado
                Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                    NavHost(navController = navController, startDestination = "tipos") {
                        composable("tipos") {
                            TiposScreen(viewModel) { id ->
                                navController.navigate("pokemons/$id")
                            }
                        }
                        composable("pokemons/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            PokemonsByTypeScreen(viewModel, id) { pokeId ->
                                navController.navigate("detalle/$pokeId")
                            }
                        }
                        composable("detalle/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            PokemonDetailScreen(viewModel, id)
                        }
                    }
                }
            }
        }
    }
}