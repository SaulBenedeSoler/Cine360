package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.DataBase.Tablas.Sala

class PeliculasAdminManager(context: Context) {
    /*Creamos una instancia con la base de datos*/
    private val dbHelper = DataBaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase
    private val TAG = "PeliculasAdminManager"

    /*Funcion para cerrar conexion con la base de datos*/
    fun close() {
        dbHelper.close()
    }

    /*Funcion para añdir pelicula, mediante la cual llamamos a los objetos del archivo gestor de base de datos
    * y le pasmos los datos de lata bla de forma que inicializamos una isntancia con la base de datos y insertamos los datos dentro de la tabla*/
    fun addPelicula(pelicula: Pelicula): Long {
        var newRowId: Long = -1L

        database.beginTransaction()
        try {
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

            newRowId = database.insert(DataBaseHelper.TABLE_PELICULA, null, values)

            if (newRowId == -1L) {
                Log.e(TAG, "Error al insertar la película: ${pelicula.titulo}")
                throw Exception("No se pudo insertar la película: ${pelicula.titulo}")
            } else {
                Log.d(TAG, "Película insertada con ID: $newRowId")

                val salaManager = SalaManager(dbHelper)

                val peliculaConId = pelicula.copy(id = newRowId.toInt())

                val lastSalaId = salaManager.crearSalaParaPelicula(database, peliculaConId)

                if (lastSalaId == -1L) {
                    Log.e(TAG, "Error al generar salas para la película: ${pelicula.titulo}")
                    throw Exception("No se pudieron generar las salas para la película: ${pelicula.titulo}")
                }
                Log.d(TAG, "Salas generadas exitosamente para la película con ID: $newRowId")
            }

            database.setTransactionSuccessful()

        } catch (e: Exception) {
            Log.e(TAG, "Error en la transacción de añadir película y salas: ${e.message}")
            newRowId = -1L
        } finally {
            database.endTransaction()
        }
        return newRowId
    }


    /*Funcion mediante la cual llamamos a todos los objetos de la tabla peliculas para obtener todas*/
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


    /*Funcion para obtener todas las peliculas mediante la cual llamamos a los objetos del arhcivo gestor de la base de datos
    * y realizamos una busqueda filtrada mediante el id de la pelicula para obtener unos datos especidficos*/
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


    /*Funcion para actualizar las peliculas mediante
    * el llamamiento al archivo gestior de base de datos y asignacion de los datos ya existentes
    * dentro de la base de datos para mostrarlos y editarlos, esto se hace mediante
    * una consulta en la cual se filtra la seleccion mediante el id de la pelicula*/
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
            Log.d(TAG, "Película con ID ${pelicula.id} actualizada.")
        } else {
            Log.e(TAG, "Error al actualizar la película con ID ${pelicula.id}.")
        }
        return rowsAffected
    }


    /*Funcion para eliminar las peliculas, mediante la cual realizamos una consulta
    * para obtener el id de la pelicula y realizamos el borrado de dicha pelicula de la base de datos*/
    fun deletePelicula(peliculaId: Int): Int {
        val selection = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
        val selectionArgs = arrayOf(peliculaId.toString())
        val rowsDeleted = database.delete(
            DataBaseHelper.TABLE_PELICULA,
            selection,
            selectionArgs
        )

        if (rowsDeleted > 0) {
            Log.d(TAG, "Película con ID $peliculaId eliminada.")
        } else {
            Log.e(TAG, "Error al eliminar la película con ID $peliculaId.")
        }
        return rowsDeleted
    }
}