package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.DataBase.Tablas.Sala

class SalaManager(private val dbHelper: DataBaseHelper) {
    private val TAG = "SalaManager"

    /*Funcion para obtener las salas por pelicula y horario*/
    fun obtenerSalasPorPeliculaYHorario(peliculaId: Int, horario: String): List<Sala> =
        /*Llamamos a la base de datos*/
        dbHelper.readableDatabase.use { readableDatabase ->
            Log.d(TAG, "Buscando salas para peliculaId: $peliculaId, horario: $horario")
            /*Creamos una lista que contendra las salas*/
            val salas = mutableListOf<Sala>()
            /*Realizamos una consulta la cual obtiene toods los datos de la tabla salas mediante el filtrado de pelicula y horario*/
            val cursor = readableDatabase.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_SALA} WHERE ${DataBaseHelper.COLUMN_PELICULA} = ? AND ${DataBaseHelper.COLUMN_HORARIO} = ?",
                arrayOf(peliculaId.toString(), horario)
            )
            /*Pasamos los datos al arhcivo gestor de la base de datos*/
            cursor.use {
                while (it.moveToNext()) {
                    val sala = Sala(
                        it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SALA_ID)),
                        it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SALA_NOMBRE)),
                        it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_MAXIMOASIENTOS)),
                        it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_MAXIMOFILAS)),
                        peliculaId,
                        it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRECIO_SALA)),
                        it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_HORARIO))
                    )
                    /*ñadimos los datos a la sala*/
                    salas.add(sala)
                    Log.d(TAG, "Sala encontrada: $sala")
                }
            }
            Log.d(TAG, "Salas encontradas: ${salas.size}")
            salas
        }


    fun obtenerSalasPorPeliculaYHorarioYSala(peliculaId: Int, horario: String, salaNombre: String): List<Sala> =
        dbHelper.readableDatabase.use { readableDatabase ->
            Log.d(TAG, "Buscando salas para peliculaId: $peliculaId, horario: $horario, sala: $salaNombre")
            val salas = mutableListOf<Sala>()
            val cursor = readableDatabase.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_SALA} WHERE ${DataBaseHelper.COLUMN_PELICULA} = ? AND ${DataBaseHelper.COLUMN_HORARIO} = ? AND ${DataBaseHelper.COLUMN_SALA_NOMBRE} = ?",
                arrayOf(peliculaId.toString(), horario, salaNombre)
            )
            cursor.use {
                while (it.moveToNext()) {
                    val sala = Sala(
                        it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SALA_ID)),
                        it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SALA_NOMBRE)),
                        it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_MAXIMOASIENTOS)),
                        it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_MAXIMOFILAS)),
                        peliculaId,
                        it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRECIO_SALA)),
                        it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_HORARIO))
                    )
                    salas.add(sala)
                    Log.d(TAG, "Sala encontrada: $sala")
                }
            }
            Log.d(TAG, "Salas encontradas: ${salas.size} con los criterios especificados.")
            salas
        }

    /*FUncion para crear salas asignadas a peliculas*/
    fun crearSalaParaPelicula(db: SQLiteDatabase, pelicula: Pelicula): Long {
        /*Declaramos los horarios y indicamos que el id nuevo no debe existir*/
        val horarios = listOf("10:00", "14:30", "19:00")
        var lastId: Long = -1
        /*Iteramos sobre los horarios y declaramos los datos a insertar*/
        for (horario in horarios) {
            val sala = Sala(
                id = 0,
                nombre = "Sala ${pelicula.titulo.take(3).uppercase()}",
                maximoAsientos = 50,
                maximoFilas = 5,
                peliculaId = pelicula.id.toInt(),
                precio = 8.5,
                horario = horario
            )
            /*Usamos la funcion para insertar la sala*/
            val id = insertarSala(db, sala)

            val asientosIniciales = (0 until sala.maximoAsientos).joinToString(",") { "O" }
            actualizarAsientos(db, id, asientosIniciales)

            lastId = id
        }

        return lastId
    }

    /*Funcion para crear sala*/
    fun insertarSala(db: SQLiteDatabase, sala: Sala): Long {
        /*Llamamos a los datos del archivo gestor de base de datos y a los datos de la tabla*/
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_SALA_NOMBRE, sala.nombre)
            put(DataBaseHelper.COLUMN_MAXIMOASIENTOS, sala.maximoAsientos)
            put(DataBaseHelper.COLUMN_MAXIMOFILAS, sala.maximoFilas)
            put(DataBaseHelper.COLUMN_PELICULA, sala.peliculaId)
            put(DataBaseHelper.COLUMN_PRECIO_SALA, sala.precio)
            put(DataBaseHelper.COLUMN_HORARIO, sala.horario)
        }
        /*Realizamos un consulta para insertar los datos en la tabla de la base de datos*/
        val insertedId = db.insert(DataBaseHelper.TABLE_SALA, null, values)
        if (insertedId == -1L) {
            Log.e(TAG, "Error al insertar sala: ${sala.nombre}")
        } else {
            Log.d(TAG, "Sala insertada con ID: $insertedId, Nombre: ${sala.nombre}")
        }
        return insertedId
    }


    /*Funcion para obtener los asientos*/
    fun obtenerAsientos(salaId: Long): String? = dbHelper.readableDatabase.use { readableDatabase ->
        /*Seleccionamos los asientos y mediante una consulta
        * realizamos la busqueda de los datos de los asientos de la tabla sala y filtrador por el id de la sala*/
        var asientos: String? = null
        val cursor = readableDatabase.rawQuery(
            "SELECT ${DataBaseHelper.COLUMN_MAXIMOASIENTOS} FROM ${DataBaseHelper.TABLE_SALA} WHERE ${DataBaseHelper.COLUMN_SALA_ID} = ?",
            arrayOf(salaId.toString())
        )
        /*Indicamos que coga toods los asientos*/
        cursor.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(DataBaseHelper.COLUMN_MAXIMOASIENTOS)
                if (columnIndex != -1) {
                    asientos = it.getString(columnIndex)
                }
            }
        }
        asientos
    }
    /*Funcion para actualizar los asientos*/
    fun actualizarAsientos(db: SQLiteDatabase, salaId: Long, asientos: String): Int {
        /*Llamamos a los asientos del archivo gestor de base de datos y le pasamos los datos de los asientos*/
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_MAXIMOASIENTOS, asientos)
        }
        /*Actualizamos los datos de la tabla sala*/
        val updatedRows = db.update(
            DataBaseHelper.TABLE_SALA,
            values,
            "${DataBaseHelper.COLUMN_SALA_ID} = ?",
            arrayOf(salaId.toString())
        )
        Log.d(TAG, "Asientos actualizados para sala ID $salaId.  Filas afectadas: $updatedRows")
        return updatedRows
    }
    /*Funcion para obtener las salas por id*/
    fun obtenerSalaPorId(db: SQLiteDatabase, id: Long): Sala? {
        /*Realizamos una consulta filtrando la busqueda por el id de la sala*/
        val cursor = db.query(
            DataBaseHelper.TABLE_SALA,
            null,
            "${DataBaseHelper.COLUMN_SALA_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        /*Llamamos a la tabla que contiene los datos y se lo pasamos todo a los objetos
        del archivo gestor de la base de datos*/
        var sala: Sala? = null
        if (cursor.moveToFirst()) {
            sala = Sala(
                cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SALA_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SALA_NOMBRE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_MAXIMOASIENTOS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_MAXIMOFILAS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PRECIO_SALA)),
                cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_HORARIO))
            )
        }
        cursor.close()
        return sala
    }


}