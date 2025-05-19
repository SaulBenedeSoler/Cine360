package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.DataBase.Tablas.Sala

class SalaManager(private val dbHelper: DataBaseHelper) {
    private val TAG = "SalaManager"

    fun obtenerSalasPorPeliculaYHorario(peliculaId: Int, horario: String): List<Sala> =
        dbHelper.readableDatabase.use { readableDatabase ->
            Log.d(TAG, "Buscando salas para peliculaId: $peliculaId, horario: $horario")
            val salas = mutableListOf<Sala>()
            val cursor = readableDatabase.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_SALA} WHERE ${DataBaseHelper.COLUMN_PELICULA} = ? AND ${DataBaseHelper.COLUMN_HORARIO} = ?",
                arrayOf(peliculaId.toString(), horario)
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

    fun crearSalaParaPelicula(db: SQLiteDatabase, pelicula: Pelicula): Long {
        val horarios = listOf("10:00", "14:30", "19:00")
        var lastId: Long = -1

        for (horario in horarios) {
            val sala = Sala(
                id = 0,
                nombre = "Sala ${pelicula.titulo.take(3).uppercase()}",
                maximoAsientos = 80,
                maximoFilas = 8,
                peliculaId = pelicula.id.toInt(),
                precio = 8.5,
                horario = horario
            )

            val id = insertarSala(db, sala)

            val asientosIniciales = (0 until sala.maximoAsientos).joinToString(",") { "O" }
            actualizarAsientos(db, id, asientosIniciales)

            lastId = id
        }

        return lastId
    }

    fun liberarAsiento(salaId: Long, asientoNumero: Int): Int {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val asientosActuales = obtenerAsientos(salaId) ?: return 0

            val asientosLista = asientosActuales.split(",").toMutableList()

            if (asientoNumero >= 0 && asientoNumero < asientosLista.size) {
                asientosLista[asientoNumero] = "O"

                val nuevosAsientos = asientosLista.joinToString(",")
                actualizarAsientos(db, salaId, nuevosAsientos)

                db.setTransactionSuccessful()
                return 1
            } else {
                return 0
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al liberar asiento: ${e.message}")
            return 0
        } finally {
            db.endTransaction()
        }
    }

    fun insertarSala(db: SQLiteDatabase, sala: Sala): Long {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_SALA_NOMBRE, sala.nombre)
            put(DataBaseHelper.COLUMN_MAXIMOASIENTOS, sala.maximoAsientos)
            put(DataBaseHelper.COLUMN_MAXIMOFILAS, sala.maximoFilas)
            put(DataBaseHelper.COLUMN_PELICULA, sala.peliculaId)
            put(DataBaseHelper.COLUMN_PRECIO_SALA, sala.precio)
            put(DataBaseHelper.COLUMN_HORARIO, sala.horario)
        }
        val insertedId = db.insert(DataBaseHelper.TABLE_SALA, null, values)
        if (insertedId == -1L) {
            Log.e(TAG, "Error al insertar sala: ${sala.nombre}")
        } else {
            Log.d(TAG, "Sala insertada con ID: $insertedId, Nombre: ${sala.nombre}")
        }
        return insertedId
    }

    fun actualizarSala(sala: Sala): Int = dbHelper.writableDatabase.use { writableDatabase ->
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_SALA_NOMBRE, sala.nombre)
            put(DataBaseHelper.COLUMN_MAXIMOASIENTOS, sala.maximoAsientos)
            put(DataBaseHelper.COLUMN_MAXIMOFILAS, sala.maximoFilas)
            put(DataBaseHelper.COLUMN_PELICULA, sala.peliculaId)
            put(DataBaseHelper.COLUMN_PRECIO_SALA, sala.precio)
            put(DataBaseHelper.COLUMN_HORARIO, sala.horario)
        }
        val rowsUpdated = writableDatabase.update(
            DataBaseHelper.TABLE_SALA,
            values,
            "${DataBaseHelper.COLUMN_SALA_ID} = ?",
            arrayOf(sala.id.toString())
        )
        Log.d(TAG, "Sala actualizada: ${sala.nombre}, Filas afectadas: $rowsUpdated")
        return rowsUpdated
    }

    fun obtenerAsientos(salaId: Long): String? = dbHelper.readableDatabase.use { readableDatabase ->
        var asientos: String? = null
        val cursor = readableDatabase.rawQuery(
            "SELECT ${DataBaseHelper.COLUMN_MAXIMOASIENTOS} FROM ${DataBaseHelper.TABLE_SALA} WHERE ${DataBaseHelper.COLUMN_SALA_ID} = ?",
            arrayOf(salaId.toString())
        )
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

    fun obtenerSalasPorPelicula(db: SQLiteDatabase, peliculaId: Int): List<Sala> {
        Log.d(TAG, "Buscando salas para peliculaId: $peliculaId")
        val salas = mutableListOf<Sala>()
        val cursor = db.rawQuery(
            "SELECT * FROM ${DataBaseHelper.TABLE_SALA} WHERE ${DataBaseHelper.COLUMN_PELICULA} = ?",
            arrayOf(peliculaId.toString())
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
        Log.d(TAG, "Salas encontradas: ${salas.size}")
        return salas
    }


    fun actualizarAsientos(db: SQLiteDatabase, salaId: Long, asientos: String): Int {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_MAXIMOASIENTOS, asientos)
        }
        val updatedRows = db.update(
            DataBaseHelper.TABLE_SALA,
            values,
            "${DataBaseHelper.COLUMN_SALA_ID} = ?",
            arrayOf(salaId.toString())
        )
        Log.d(TAG, "Asientos actualizados para sala ID $salaId.  Filas afectadas: $updatedRows")
        return updatedRows
    }

    fun obtenerSalaPorId(db: SQLiteDatabase, id: Long): Sala? {
        val cursor = db.query(
            DataBaseHelper.TABLE_SALA,
            null,
            "${DataBaseHelper.COLUMN_SALA_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

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

    fun obtenerSalaIdPorNombreYHorario(db: SQLiteDatabase, nombre: String, horario: String): Long {
        var salaId: Long = -1

        val selection = "${DataBaseHelper.COLUMN_SALA_NOMBRE} = ? AND ${DataBaseHelper.COLUMN_HORARIO} = ?"
        val selectionArgs = arrayOf(nombre, horario)

        val cursor = db.query(
            DataBaseHelper.TABLE_SALA,
            arrayOf(DataBaseHelper.COLUMN_SALA_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_SALA_ID)
            if (idIndex >= 0) {
                salaId = cursor.getLong(idIndex)
            }
        }

        cursor.close()
        return salaId
    }
}
