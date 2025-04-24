package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Director

class DirectorManager(val dbHelper: DataBaseHelper) {

    private val TAG = "DirectorManager"

    fun insertarDirectores(db: SQLiteDatabase, directores: List<Director>): List<Long> {
        val ids = mutableListOf<Long>()
        try {
            db.beginTransaction()
            directores.forEach { director ->
                val values = ContentValues().apply {
                    put(DataBaseHelper.COLUMN_DIRECTOR_NOMBRE, director.nombre)
                    put(DataBaseHelper.COLUMN_DIRECTOR_APELLIDO, director.apellido) // Añadido el apellido
                    put(DataBaseHelper.COLUMN_PELICULAID, director.peliculaId)
                }
                val id = db.insert(DataBaseHelper.TABLE_DIRECTOR, null, values)
                if (id == -1L) {
                    throw SQLiteException("Error al insertar director: ${director.nombre}")
                }
                ids.add(id)
            }
            db.setTransactionSuccessful()
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error al insertar directores: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return ids
    }

    fun insertarDirectoresPrecargados(db: SQLiteDatabase) {
        val directores = listOf(
            /*Directores Películas Semana 1*/
            Director(1, "Chris", "Columbus", 1),
            Director(2, "Olivier", "Nakache", 2),
            Director(3, "Peter", "Farrelly", 3),
            Director(4, "Larry", "Charles", 4),
            Director(5, "Mark", "Mylod", 5),
            Director(6, "Seth", "MacFarlane", 6),
            Director(7, "Javier", "Fesser", 7),

            /*Directores Peliculas Semana 2*/
            Director(8, "Michael", "Sucsy", 8),
            Director(9, "Lasse", "Hallström", 9),
            Director(10, "Josh", "Boone", 10),
            Director(11, "James", "Cameron", 11),
            Director(12, "Joaquín", "Llamas", 12),
            Director(13, "Fernando", "Gonzalez Molina", 13),
            Director(14, "Nicole", "Kassell", 14),

            /*Directores Peliculas Semana 3*/
            Director(15, "Damien", "Leone", 15),
            Director(16, "Parker", "Finn", 16),
            Director(17, "Natalie Erika", "James", 17),
            Director(18, "Takashi", "Miike", 18),
            Director(19, "Tobe", "Hooper", 19),
            Director(20, "Sam", "Raimi", 20),
            Director(21, "Lars", "von Trier", 21),

            /*Directores Peliculas Semana 4*/
            Director(22, "John", "McTiernan", 22),
            Director(23, "James", "Cameron", 23),
            Director(24, "Quentin", "Tarantino", 24),
            Director(25, "George", "Miller", 25),
            Director(26, "Lana", "Wachowski", 26),
            Director(27, "Christopher", "McQuarrie", 27),
            Director(28, "Richard", "Donner", 28)
        )
        insertarDirectores(db, directores)
    }

    fun obtenerDirectoresPorPeliculaId(peliculaId: Long): List<Director> {
        val directores = mutableListOf<Director>()
        dbHelper.readableDatabase.use { db ->
            val cursor = db.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_DIRECTOR} WHERE ${DataBaseHelper.COLUMN_PELICULAID} = ?",
                arrayOf(peliculaId.toString())
            )
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DIRECTOR_ID))
                        val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DIRECTOR_NOMBRE))
                        val apellido = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DIRECTOR_APELLIDO))
                        val peliculaId = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULAID))

                        val director = Director(id, nombre, apellido ,peliculaId)
                        directores.add(director)
                    } while (it.moveToNext())
                }
            }
        }
        return directores
    }


}