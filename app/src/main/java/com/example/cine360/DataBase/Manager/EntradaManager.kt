package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Entrada
import com.example.cine360.DataBase.Tablas.Sala

class EntradaManager(private val dbHelper: DataBaseHelper) {


    /*Funcion para crear la entrada*/
    fun crearEntrada(
        userId: Int,
        peliculaId: Int,
        sala: String,
        asiento: Int,
        fila: Int,
        horario: String,
        nombrePelicula: String
    ): Long {
        /*Obtenemos instancia de la base de datos*/
        val db = dbHelper.writableDatabase
        /*Obtenemos un objeto para almacenar los diferentes valores que vamos a almacenar en la base de datos*/
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_USERID, userId)
            put(DataBaseHelper.COLUMN_PELI_ID, peliculaId)
            put(DataBaseHelper.COLUMN_SALA, sala)
            put(DataBaseHelper.COLUMN_ASIENTO, asiento)
            put(DataBaseHelper.COLUMN_FILA, fila)
            put(DataBaseHelper.COLUMN_SALA_HORA, horario)
            put(DataBaseHelper.COLUMN_PELICULA_NOMBRE, nombrePelicula)
        }
        /*Obtenemos el id de la entrada*/
        val id = db.insert(DataBaseHelper.TABLE_ENTRADA, null, values)
        Log.d("EntradaManager", "Nueva entrada creada con ID: $id")
        return id
    }
    /*Funcion para obtener todas las entrads*/
    fun obtenerTodasLasEntradas(): List<Entrada> {
        /*Creamos una entrada para almacenar las entradas*/
        val entradas = mutableListOf<Entrada>()
        val db = dbHelper.readableDatabase
        /*Hacemos una consulta para obtener todas las entradas de la tabla*/
        val cursor = db.query(
            DataBaseHelper.TABLE_ENTRADA,
            null,
            null,
            null,
            null,
            null,
            null
        )
        /*Itermos sobre el cursor para leer cada fila*/
        while (cursor.moveToNext()) {
            val entrada = cursorToEntrada(cursor)
            entradas.add(entrada)
        }

        cursor.close()
        return entradas
    }
    /*Funcion para obtener las entradas por usuario*/
    fun obtenerEntradasPorUsuario(userId: Int): List<Entrada> {
        /*Variables de entradas para obtener una lista con todas las entradas*/
        val entradas = mutableListOf<Entrada>()
        /*Instnaciamso la base de datos*/
        val db = dbHelper.readableDatabase
        /*Seleccionamos el id del usuario*/
        val selection = "${DataBaseHelper.COLUMN_USERID} = ?"
        val selectionArgs = arrayOf(userId.toString())
        /*Consultamos la tabla de entradas para obtener todas*/
        val cursor = db.query(
            DataBaseHelper.TABLE_ENTRADA,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        /*Iteramos sobre la entrada para obtener todas*/
        while (cursor.moveToNext()) {
            val entrada = cursorToEntrada(cursor)
            entradas.add(entrada)
        }

        cursor.close()
        return entradas
    }
    /*Funcion usada para llamar a los datos del archivo gestor de la base de datos y obtener la entrada*/
    private fun cursorToEntrada(cursor: Cursor): Entrada {
        val idIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ENTRADA_ID)
        val userIdIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_USERID)
        val peliculaIdIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_PELI_ID)
        val salaNombreIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_SALA)
        val asientoIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ASIENTO)
        val filaIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_FILA)
        val horarioIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_SALA_HORA)
        val nombrePeliculaIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_PELICULA_NOMBRE)

        val id = if (idIndex >= 0) cursor.getInt(idIndex) else 0
        val userId = if (userIdIndex >= 0) cursor.getInt(userIdIndex) else 0
        val peliculaId = if (peliculaIdIndex >= 0) cursor.getInt(peliculaIdIndex) else 0
        val salaNombre = if (salaNombreIndex >= 0) cursor.getString(salaNombreIndex) else ""
        val asiento = if (asientoIndex >= 0) cursor.getInt(asientoIndex) else null
        val fila = if (filaIndex >= 0) cursor.getInt(filaIndex) else null
        val horario = if (horarioIndex >= 0) cursor.getString(horarioIndex) else null
        val nombrePelicula = if (nombrePeliculaIndex >= 0) cursor.getString(nombrePeliculaIndex) else ""

        /*Obtenemos los datos de la sala*/
        val sala = Sala(
            id = 0,
            nombre = salaNombre,
            maximoAsientos = 0,
            maximoFilas = 0,
            horario = horario ?: "",
            precio = 0.0,
            peliculaId = peliculaId.toLong().toInt()
        )
        /*Devolvemos la entrada con todos lso datos ya registrados*/
        return Entrada(
            id = id,
            userId = userId,
            peliculaId = peliculaId,
            sala = sala,
            asiento = asiento,
            fila = fila,
            horario = horario,
            nombrePelicula = nombrePelicula
        )
    }


    /*Funcion para eliminar la entrada*/
    fun eliminarEntrada(entradaId: Int): Boolean {
        /*Declaramos una instancia con la base de datos*/
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            /*Obtenemos el id de la entrada*/
            val selection = "${DataBaseHelper.COLUMN_ENTRADA_ID} = ?"
            val selectionArgs = arrayOf(entradaId.toString())
            /*Realizamos una consulta para borra entrada de la tabla de la entrada*/
            val deletedRows = db.delete(DataBaseHelper.TABLE_ENTRADA, selection, selectionArgs)
            db.setTransactionSuccessful()
            return deletedRows > 0
        } catch (e: Exception) {
            Log.e("EntradaManager", "Error al eliminar entrada: ${e.message}")
            return false
        } finally {
            db.endTransaction()
        }
    }
}