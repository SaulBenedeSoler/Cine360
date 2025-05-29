package com.example.cine360.DataBase.Tablas


data class Sala(
    val id: Int = 0,
    val nombre: String,
    val maximoAsientos: Int,
    val peliculaId: Int,
    val maximoFilas: Int,
    val precio: Double,
    val horario: String,
)
