package com.example.practica09.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    @GET("pokemon/{nombre}")
    suspend fun obtener(@Path("nombre") nombre: String): Response<ResponseBody>

    @DELETE("pokemon/{nombre}")
    suspend fun borrar(@Path("nombre") nombre: String): Response<ResponseBody>
}

object PokeClient {
    val api: PokeApi = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .build()
        .create(PokeApi::class.java)
}