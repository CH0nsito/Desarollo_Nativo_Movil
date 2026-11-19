package com.example.practica12.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    @GET("type")
    suspend fun getTypes(): TypeListResponse

    @GET("type/{id}")
    suspend fun getPokemonsByType(@Path("id") id: String): TypeDetailResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: String): PokemonDetailResponse
}