package com.example.practica14.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcadorDao {
    @Insert
    suspend fun insertar(marcador: MarcadorEntity)

    @Query("SELECT * FROM marcadores ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<MarcadorEntity>>

    @Query("DELETE FROM marcadores")
    suspend fun borrarTodos()
}