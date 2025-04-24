package com.example.cine360.DataBase.Tablas

data class Pelicula(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val genero: String,
    val fechaLanzamiento: String,
    val duracion: Int,
    val imagen: String,
    val trailer: String,
    val semana: String?,
    val directores: List<Director> = emptyList(),
    val actores: List<Actor> = emptyList()
)

