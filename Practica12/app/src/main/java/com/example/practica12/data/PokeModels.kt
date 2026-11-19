package com.example.practica12.data

data class NamedResource(val name: String, val url: String) {
    // Extraemos el ID numérico de la URL
    val id: String get() = url.trimEnd('/').substringAfterLast('/')
}

// Pantalla 1: lista de categorías (tipos)
data class TypeListResponse(val results: List<NamedResource>)

// Pantalla 2: pokémones de la categoría seleccionada
data class TypeDetailResponse(val pokemon: List<PokemonSlot>)
data class PokemonSlot(val pokemon: NamedResource)

// Pantalla 3: detalle del pokémon seleccionado
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val stats: List<StatSlot>,
    val types: List<TypeSlot>
)
data class StatSlot(val base_stat: Int, val stat: NamedResource)
data class TypeSlot(val type: NamedResource)