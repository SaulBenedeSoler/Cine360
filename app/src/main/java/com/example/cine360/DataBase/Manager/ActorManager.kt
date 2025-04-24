package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Actor

class ActorManager (val dbHelper: DataBaseHelper){

    private val TAG = "ActorManager"

    fun insertarActores(db: SQLiteDatabase, actores: List<Actor>): List<Long> {
        val ids = mutableListOf<Long>()
        try {
            db.beginTransaction()
            actores.forEach { actor ->
                val values = ContentValues().apply {
                    put(DataBaseHelper.COLUMN_ACTOR_NOMBRE, actor.nombre)
                    put(DataBaseHelper.COLUMN_ACTOR_APELLIDO, actor.apellido)
                    put(DataBaseHelper.COLUMN_PELICULA_ID, actor.peliculaId)
                }
                val id = db.insert(DataBaseHelper.TABLE_ACTOR, null, values)
                if (id == -1L) {
                    throw SQLiteException("Error al insertar actor: ${actor.nombre}")
                }
                ids.add(id)
            }
            db.setTransactionSuccessful()
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error al insertar actor: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return ids
    }

    fun insertarActoresPrecargados(db: SQLiteDatabase) {
        val actores = listOf(
            /*actor Películas Semana 1*/
            Actor(1, "Macaulay", "Culkin", 1),
            Actor(2, "Omar", "Sy", 2),
            Actor(3, "Jim", "Carrey", 3),
            Actor(4, "Sacha", "Baron", 4),
            Actor(5, "Sacha", "Baron", 5),
            Actor(6, "Mark", "Whalberg", 6),
            Actor(7, "Javier", "Gutierrez", 7),

            /*actor Peliculas Semana 2*/
            Actor(8, "Rachel", "McAdams", 8),
            Actor(9, "Channing", "Tatum", 9),
            Actor(10, "Shailene", "Woodley", 10),
            Actor(11, "Kate", "Winslet", 11),
            Actor(12, "Paloma", "Bloyd", 12),
            Actor(13, "Mario", "Casas", 13),
            Actor(14, "Kate", "Hudson", 14),

            /*actor Peliculas Semana 3*/
            Actor(15, "David Howard", "Thornton", 15),
            Actor(16, "Sosie", "Bacon", 16),
            Actor(17, "Emily", "Mortimer", 17),
            Actor(18, "Ryo", "Ishabashi", 18),
            Actor(19, "Marilyn", "Burns", 19),
            Actor(20, "Alison", "Lohman", 20),
            Actor(21, "Harvey", "Stephens", 21),

            /*actor Peliculas Semana 4*/
            Actor(22, "Bruce", "Willis", 22),
            Actor(23, "Edward", "Furlong", 23),
            Actor(24, "Uma", "Thurman", 24),
            Actor(25, "Tom", "Hardy", 25),
            Actor(26, "Keanu", "Reeves", 26),
            Actor(27, "Tom", "Cruise", 27),
            Actor(28, "Kirk", "Alyn", 28)
        )
        insertarActores(db, actores)
    }


    fun obtenerActoresesPorPeliculaId(peliculaId: Long): List<Actor> {
        val actores = mutableListOf<Actor>()
        dbHelper.readableDatabase.use { db ->
            val cursor = db.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_ACTOR} WHERE ${DataBaseHelper.COLUMN_PELICULA_ID} = ?",
                arrayOf(peliculaId.toString())
            )
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ACTOR_ID))
                        val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ACTOR_NOMBRE))
                        val apellido = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ACTOR_APELLIDO))
                        val peliculaId = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_ID))

                        val actor = Actor(id, nombre, apellido ,peliculaId)
                        actores.add(actor)
                    } while (it.moveToNext())
                }
            }
        }
        return actores
    }



}