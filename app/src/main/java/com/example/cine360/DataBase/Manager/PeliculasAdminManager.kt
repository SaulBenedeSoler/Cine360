package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Pelicula

class PeliculasAdminManager(context: Context) {

    private val dbHelper = DataBaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase


    fun close() {
        dbHelper.close()
    }


    fun addPelicula(pelicula: Pelicula): Long {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUM_TITULO, pelicula.titulo)
            put(DataBaseHelper.COLUMN_DESCRIPCION, pelicula.descripcion)
            put(DataBaseHelper.COLUMN_GENERO, pelicula.genero)
            put(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO, pelicula.fechaLanzamiento)
            put(DataBaseHelper.COLUMN_DURACION, pelicula.duracion)
            put(DataBaseHelper.COLUMN_PELICULA_IMAGEN, pelicula.imagen)
            put(DataBaseHelper.COLUMN_TRAILER, pelicula.trailer)
            put(DataBaseHelper.COLUMN_SEMANA, pelicula.semana)
        }

        val newRowId = database.insert(DataBaseHelper.TABLE_PELICULA, null, values)
        if (newRowId == -1L) {
            Log.e("AdminPeliculaManager", "Error al insertar la película: ${pelicula.titulo}")
        } else {
            Log.d("AdminPeliculaManager", "Película insertada con ID: $newRowId")
        }
        return newRowId
    }


    fun getAllPeliculas(): MutableList<Pelicula> {
        val peliculas = mutableListOf<Pelicula>()
        val projection = arrayOf(
            DataBaseHelper.COLUMN_Pelicula_ID,
            DataBaseHelper.COLUM_TITULO,
            DataBaseHelper.COLUMN_DESCRIPCION,
            DataBaseHelper.COLUMN_GENERO,
            DataBaseHelper.COLUMN_FECHA_LANZAMIENTO,
            DataBaseHelper.COLUMN_DURACION,
            DataBaseHelper.COLUMN_PELICULA_IMAGEN,
            DataBaseHelper.COLUMN_TRAILER,
            DataBaseHelper.COLUMN_SEMANA
        )

        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_PELICULA,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_Pelicula_ID)).toInt()
                val titulo = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUM_TITULO))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPCION))
                val genero = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_GENERO))
                val fechaLanzamiento = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO))
                val duracion = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DURACION))
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_IMAGEN))
                val trailer = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_TRAILER))
                val semana = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA))

                val pelicula = Pelicula(id, titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana)
                peliculas.add(pelicula)
            }
        }
        return peliculas
    }


    fun getPeliculaById(peliculaId: Int): Pelicula? {
        val projection = arrayOf(
            DataBaseHelper.COLUMN_Pelicula_ID,
            DataBaseHelper.COLUM_TITULO,
            DataBaseHelper.COLUMN_DESCRIPCION,
            DataBaseHelper.COLUMN_GENERO,
            DataBaseHelper.COLUMN_FECHA_LANZAMIENTO,
            DataBaseHelper.COLUMN_DURACION,
            DataBaseHelper.COLUMN_PELICULA_IMAGEN,
            DataBaseHelper.COLUMN_TRAILER,
            DataBaseHelper.COLUMN_SEMANA
        )

        val selection = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
        val selectionArgs = arrayOf(peliculaId.toString())

        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_PELICULA,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_Pelicula_ID)).toInt()
                val titulo = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUM_TITULO))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPCION))
                val genero = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_GENERO))
                val fechaLanzamiento = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO))
                val duracion = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DURACION))
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_IMAGEN))
                val trailer = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_TRAILER))
                val semana = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA))

                return Pelicula(id, titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana)
            }
        }
        return null
    }


    fun updatePelicula(pelicula: Pelicula): Int {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUM_TITULO, pelicula.titulo)
            put(DataBaseHelper.COLUMN_DESCRIPCION, pelicula.descripcion)
            put(DataBaseHelper.COLUMN_GENERO, pelicula.genero)
            put(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO, pelicula.fechaLanzamiento)
            put(DataBaseHelper.COLUMN_DURACION, pelicula.duracion)
            put(DataBaseHelper.COLUMN_PELICULA_IMAGEN, pelicula.imagen)
            put(DataBaseHelper.COLUMN_TRAILER, pelicula.trailer)
            put(DataBaseHelper.COLUMN_SEMANA, pelicula.semana)
        }

        val selection = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
        val selectionArgs = arrayOf(pelicula.id.toString())

        val rowsAffected = database.update(
            DataBaseHelper.TABLE_PELICULA,
            values,
            selection,
            selectionArgs
        )

        if (rowsAffected > 0) {
            Log.d("AdminPeliculaManager", "Película con ID ${pelicula.id} actualizada.")
        } else {
            Log.e("AdminPeliculaManager", "Error al actualizar la película con ID ${pelicula.id}.")
        }
        return rowsAffected
    }


    fun deletePelicula(peliculaId: Int): Int {
        val selection = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
        val selectionArgs = arrayOf(peliculaId.toString())

        val rowsDeleted = database.delete(
            DataBaseHelper.TABLE_PELICULA,
            selection,
            selectionArgs
        )

        if (rowsDeleted > 0) {
            Log.d("AdminPeliculaManager", "Película con ID $peliculaId eliminada.")
        } else {
            Log.e("AdminPeliculaManager", "Error al eliminar la película con ID $peliculaId.")
        }
        return rowsDeleted
    }
}