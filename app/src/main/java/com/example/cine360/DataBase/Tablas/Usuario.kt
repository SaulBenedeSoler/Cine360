package com.example.cine360.DataBase.Tablas

data class Usuario(
    val id: Int = 0,
    val username: String,
    val password: String,
    val isAdmin: Boolean = false,
    val nombre: String = "",
    val email: String = "",


)
