package com.example.cine360.DataBase.Tablas

data class EntradaComida(
    val id: Int = 0,
    val usuarioId: Int,
    val comidaId: Int,
    val nombrecomida: String,
    val descripcioncomida: String?,
    val preciocomida: Double,
    val imagencomida: String?

)
