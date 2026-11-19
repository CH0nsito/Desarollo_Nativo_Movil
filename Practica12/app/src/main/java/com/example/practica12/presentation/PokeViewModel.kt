package com.example.practica12.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica12.data.NamedResource
import com.example.practica12.data.PokemonDetailResponse
import com.example.practica12.data.PokeRepository
import kotlinx.coroutines.launch

class PokeViewModel : ViewModel() {
    private val repository = PokeRepository()

    var tipos = mutableStateOf<List<NamedResource>>(emptyList())
    var pokemonsPorTipo = mutableStateOf<List<NamedResource>>(emptyList())
    var pokemonDetalle = mutableStateOf<PokemonDetailResponse?>(null)

    init {
        // Cargar las categorías apenas se abra la app
        viewModelScope.launch {
            try { tipos.value = repository.getTypes() } catch (e: Exception) { }
        }
    }

    fun cargarPokemonsPorTipo(id: String) {
        viewModelScope.launch {
            try { pokemonsPorTipo.value = repository.getPokemonsByType(id) } catch (e: Exception) { }
        }
    }

    fun cargarDetallePokemon(id: String) {
        pokemonDetalle.value = null // limpiamos la vista anterior
        viewModelScope.launch {
            try { pokemonDetalle.value = repository.getPokemonDetail(id) } catch (e: Exception) { }
        }
    }
}