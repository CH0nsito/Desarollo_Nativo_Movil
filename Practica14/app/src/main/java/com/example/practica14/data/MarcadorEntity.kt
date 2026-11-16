package com.example.practica14.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marcadores")
data class MarcadorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitud: Double,
    val longitud: Double,
    val titulo: String
)