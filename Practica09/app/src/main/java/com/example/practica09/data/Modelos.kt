package com.example.practica09.data

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerializedName("base_experience") val baseExperience: Int?,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>,
    val sprites: Sprites
)

data class NamedRef(val name: String, val url: String)
data class TypeSlot(val slot: Int, val type: NamedRef)
data class StatSlot(@SerializedName("base_stat") val baseStat: Int, val stat: NamedRef)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("back_default") val backDefault: String?,
    val other: Other?
)
data class Other(@SerializedName("official-artwork") val officialArtwork: Artwork?)
data class Artwork(@SerializedName("front_default") val frontDefault: String?)