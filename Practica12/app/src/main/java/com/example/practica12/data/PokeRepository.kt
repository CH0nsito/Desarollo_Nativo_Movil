package com.example.practica12.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val pokeApi: PokeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }
}

class PokeRepository {
    private val api = RetrofitClient.pokeApi

    suspend fun getTypes() = api.getTypes().results

    suspend fun getPokemonsByType(id: String) =
        api.getPokemonsByType(id).pokemon.map { it.pokemon }

    suspend fun getPokemonDetail(id: String) = api.getPokemonDetail(id)
}