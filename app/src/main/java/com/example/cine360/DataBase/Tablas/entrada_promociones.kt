package com.example.cine360.DataBase.Tablas

data class EntradaPromociones(
    val id: Int = 0,
    val usuarioId: Int,
    val promocionId: Int,
    val nombrePromocion: String,
    val descripcionPromocion: String?,
    val precioPromocion: Double,
    val imagenPromocion: String?
)