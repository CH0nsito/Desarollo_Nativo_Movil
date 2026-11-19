package com.example.practica06

data class Contacto(
    val id: Int,
    val nombre: String,
    val telefono: String
)

fun obtenerContactosDummy(): List<Contacto> {
    return listOf(
        Contacto(1, "Ana García", "555-1001"),
        Contacto(2, "Luis Pérez", "555-1002"),
        Contacto(3, "María López", "555-1003"),
        Contacto(4, "Carlos Ramírez", "555-1004"),
        Contacto(5, "Sofía Torres", "555-1005"),
        Contacto(6, "Jorge Medina", "555-1006"),
        Contacto(7, "Laura Salazar", "555-1007"),
        Contacto(8, "Pedro Castillo", "555-1008"),
        Contacto(9, "Elena Rojas", "555-1009"),
        Contacto(10, "Diego Castro", "555-1010"),
        Contacto(11, "Valeria Silva", "555-1011"),
        Contacto(12, "Ricardo Soto", "555-1012")
    )
}