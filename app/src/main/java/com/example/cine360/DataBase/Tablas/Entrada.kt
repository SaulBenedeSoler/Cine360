package com.example.cine360.DataBase.Tablas

data class Entrada(
    val id: Int = 0,
    val userId: Int,
    val peliculaId: Int,
    val sala: Sala?,
    val asiento: Int?,
    val fila: Int?,
    val horario: String?,
    val nombrePelicula: String
)
