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
    /*Compruebo mediante una instancia a la bae de datos que es un administrador,
    * esto se hace mediante la comprobacion primero de si existe realmente un usuario y lo hace
    * mediante el uso de la tabla de usuarios y el dato necesario para este,
    * en caso de serlo cierra esta conexion con la base de datos*/
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
        /*Si no existe el administrador se crea uno pordefecto y mostramos que ha sido creado*/
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
        /*Cerramos conexion con base de datos*/
        db.close()
    }

    /*Funcion para crear usuario, esto es mediante el llamamiento
    * a la tabla y obtencio de los datos lso cuales se asignan a diferentes variables
    *  y se añaden a la tabla*/
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
    /*Funcion para obtener todos los usuarios de la tabla de la base de datos*/
    fun getAllUsers(): List<Usuario> {
        /*Creamos una lista que contendra todos los usuarios almacenados en la base de datos
        * Creamos una instancia con la base de datos
        * Seleccionamos toods los usuarios de la tabla usuarios*/
        val userList = mutableListOf<Usuario>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DataBaseHelper.TABLE_USERS}"
        val cursor = db.rawQuery(query, null)
        /*Llamamos a todos los datos que contiene un usuario y lo asignamos para comprobar si es usuario*/
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID)
            val usernameIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_USERNAME)
            val passwordIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_PASSWORD)
            val isAdminIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_IS_ADMIN)
            val nombreIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_NOMBRE)
            val emailIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_EMAIL)
            /*Si es usuario normal debemos comprobar que tiene todos los datos correctos*/
            do {
                val user = Usuario(
                    id = if (idIndex != -1) cursor.getInt(idIndex) else 0,
                    username = if (usernameIndex != -1) cursor.getString(usernameIndex) else "",
                    password = if (passwordIndex != -1) cursor.getString(passwordIndex) else "",
                    isAdmin = if (isAdminIndex != -1) cursor.getInt(isAdminIndex) == 1 else false,
                    nombre = if (nombreIndex != -1) cursor.getString(nombreIndex) else "",
                    email = if (emailIndex != -1) cursor.getString(emailIndex) else ""
                )
                /*Añadimos todos los usuarios a la lista*/
                userList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userList
    }

    /*Funcion para actualizar los usuarios en la base de datos*/
    fun updateUser(user: Usuario): Int {
        /*Creamos una instancia con la base de datos
        * Asignamos cada dato de la tabla a un objeto para trabjar con esto*/
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_USERNAME, user.username)
            put(DataBaseHelper.COLUMN_PASSWORD, user.password)
            put(DataBaseHelper.COLUMN_IS_ADMIN, if (user.isAdmin) 1 else 0)
            put(DataBaseHelper.COLUMN_NOMBRE, user.nombre)
            put(DataBaseHelper.COLUMN_EMAIL, user.email)
        }
        /*Actualizamos la base de datos*/
        val result = db.update(
            DataBaseHelper.TABLE_USERS,
            values,
            "${DataBaseHelper.COLUMN_ID} = ?",
            arrayOf(user.id.toString())
        )
        db.close()
        return result
    }

    /*Funcion para eliminar usuarios
    * Mediante la compribacion de estos y un instancia con la base de datos*/
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

    /*Cerramos conexion con la base de datos*/
    fun close() {
        dbHelper.close()
    }
    /*Funcion en la cual mediante la obtencio nde los datos de la tabla
    * y la comprobacion del id podemos obtener un usuario especifico*/
    fun getUserById(userId: Int): Usuario? {
        val db = dbHelper.readableDatabase
        var user: Usuario? = null
        val cursor = db.query(
            DataBaseHelper.TABLE_USERS,
            arrayOf(
                DataBaseHelper.COLUMN_ID,
                DataBaseHelper.COLUMN_USERNAME,
                DataBaseHelper.COLUMN_PASSWORD,
                DataBaseHelper.COLUMN_IS_ADMIN,
                DataBaseHelper.COLUMN_NOMBRE,
                DataBaseHelper.COLUMN_EMAIL
            ),
            "${DataBaseHelper.COLUMN_ID} = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        cursor.use {
            if (it.moveToFirst()) {
                user = Usuario(
                    id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ID)),
                    username = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_USERNAME)),
                    password = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PASSWORD)),
                    isAdmin = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_IS_ADMIN)) == 1,
                    nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NOMBRE)),
                    email = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_EMAIL))
                )
            }
        }
        db.close()
        return user
    }
}