package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Entrada
import com.example.cine360.DataBase.Tablas.Sala

class EntradaManager(private val dbHelper: DataBaseHelper) {

    fun crearEntrada(
        userId: Int,
        peliculaId: Int,
        sala: String,
        asiento: Int,
        fila: Int,
        horario: String,
        nombrePelicula: String
    ): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_USERID, userId)
            put(DataBaseHelper.COLUMN_PELI_ID, peliculaId)
            put(DataBaseHelper.COLUMN_SALA, sala)
            put(DataBaseHelper.COLUMN_ASIENTO, asiento)
            put(DataBaseHelper.COLUMN_FILA, fila)
            put(DataBaseHelper.COLUMN_SALA_HORA, horario)
            put(DataBaseHelper.COLUMN_PELICULA_NOMBRE, nombrePelicula)
        }

        val id = db.insert(DataBaseHelper.TABLE_ENTRADA, null, values)
        Log.d("EntradaManager", "Nueva entrada creada con ID: $id")
        return id
    }

    fun obtenerTodasLasEntradas(): List<Entrada> {
        val entradas = mutableListOf<Entrada>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DataBaseHelper.TABLE_ENTRADA,
            null,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val entrada = cursorToEntrada(cursor)
            entradas.add(entrada)
        }

        cursor.close()
        return entradas
    }

    fun obtenerEntradasPorUsuario(userId: Int): List<Entrada> {
        val entradas = mutableListOf<Entrada>()
        val db = dbHelper.readableDatabase

        val selection = "${DataBaseHelper.COLUMN_USERID} = ?"
        val selectionArgs = arrayOf(userId.toString())

        val cursor = db.query(
            DataBaseHelper.TABLE_ENTRADA,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val entrada = cursorToEntrada(cursor)
            entradas.add(entrada)
        }

        cursor.close()
        return entradas
    }

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

        val sala = Sala(
            id = 0,
            nombre = salaNombre,
            maximoAsientos = 0,
            maximoFilas = 0,
            horario = horario ?: "",
            precio = 0.0,
            peliculaId = peliculaId.toLong().toInt()
        )

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



    fun eliminarEntrada(entradaId: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val selection = "${DataBaseHelper.COLUMN_ENTRADA_ID} = ?"
            val selectionArgs = arrayOf(entradaId.toString())

            val deletedRows = db.delete(DataBaseHelper.TABLE_ENTRADA, selection, selectionArgs)
            Log.d("EntradaManager", "Filas eliminadas: $deletedRows")
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