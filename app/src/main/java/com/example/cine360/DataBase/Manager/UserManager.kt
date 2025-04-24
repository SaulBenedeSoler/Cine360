package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Usuario

class UserManager(private val context: Context) {

    private val dbHelper = DataBaseHelper(context)

    init {

        checkAndCreateDefaultAdmin()
    }

    private fun checkAndCreateDefaultAdmin() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DataBaseHelper.TABLE_USERS,
            null,
            "${DataBaseHelper.COLUMN_USERNAME} = ?",
            arrayOf("admin"),
            null, null, null
        )

        val adminExists = cursor.count > 0
        cursor.close()

        if (!adminExists) {
            val adminValues = ContentValues().apply {
                put(DataBaseHelper.COLUMN_USERNAME, "admin")
                put(DataBaseHelper.COLUMN_PASSWORD, "admin123")
                put(DataBaseHelper.COLUMN_IS_ADMIN, 1)
                put(DataBaseHelper.COLUMN_NOMBRE, "Administrador")
                put(DataBaseHelper.COLUMN_EMAIL, "admin@example.com")
            }

            dbHelper.writableDatabase.insert(DataBaseHelper.TABLE_USERS, null, adminValues)
            Log.d("UserManager", "Administrador por defecto creado")
        }

        db.close()
    }


    fun addUser(
        username: String,
        password: String,
        nombre: String,
        email: String,
        isAdmin: Boolean = false
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_USERNAME, username)
            put(DataBaseHelper.COLUMN_PASSWORD, password)
            put(DataBaseHelper.COLUMN_IS_ADMIN, if (isAdmin) 1 else 0)
            put(DataBaseHelper.COLUMN_NOMBRE, nombre)
            put(DataBaseHelper.COLUMN_EMAIL, email)
        }

        val result = db.insert(DataBaseHelper.TABLE_USERS, null, values)
        db.close()
        return result
    }


    fun checkUser(username: String, password: String): Pair<Boolean, Boolean> {
        val db = dbHelper.readableDatabase
        val selection =
            "${DataBaseHelper.COLUMN_USERNAME} = ? AND ${DataBaseHelper.COLUMN_PASSWORD} = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor =
            db.query(DataBaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null)

        val exists = cursor.count > 0
        var isAdmin = false

        if (exists && cursor.moveToFirst()) {
            val adminIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_IS_ADMIN)
            if (adminIndex != -1) {
                isAdmin = cursor.getInt(adminIndex) == 1
            }
        }

        cursor.close()
        db.close()
        return Pair(exists, isAdmin)
    }


    fun getAllUsers(): List<Usuario> {
        val userList = mutableListOf<Usuario>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DataBaseHelper.TABLE_USERS}"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID)
            val usernameIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_USERNAME)
            val passwordIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_PASSWORD)
            val isAdminIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_IS_ADMIN)
            val nombreIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_NOMBRE)
            val emailIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_EMAIL)

            do {
                val user = Usuario(
                    id = if (idIndex != -1) cursor.getInt(idIndex) else 0,
                    username = if (usernameIndex != -1) cursor.getString(usernameIndex) else "",
                    password = if (passwordIndex != -1) cursor.getString(passwordIndex) else "",
                    isAdmin = if (isAdminIndex != -1) cursor.getInt(isAdminIndex) == 1 else false,
                    nombre = if (nombreIndex != -1) cursor.getString(nombreIndex) else "",
                    email = if (emailIndex != -1) cursor.getString(emailIndex) else ""
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userList
    }


    fun updateUser(user: Usuario): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_USERNAME, user.username)
            put(DataBaseHelper.COLUMN_PASSWORD, user.password)
            put(DataBaseHelper.COLUMN_IS_ADMIN, if (user.isAdmin) 1 else 0)
            put(DataBaseHelper.COLUMN_NOMBRE, user.nombre)
            put(DataBaseHelper.COLUMN_EMAIL, user.email)
        }

        val result = db.update(
            DataBaseHelper.TABLE_USERS,
            values,
            "${DataBaseHelper.COLUMN_ID} = ?",
            arrayOf(user.id.toString())
        )
        db.close()
        return result
    }


    fun deleteUser(userId: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DataBaseHelper.TABLE_USERS,
            "${DataBaseHelper.COLUMN_ID} = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return result
    }


    fun close() {
        dbHelper.close()
    }
}