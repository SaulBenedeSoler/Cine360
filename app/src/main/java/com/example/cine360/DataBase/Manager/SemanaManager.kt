package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper

class SemanaManager(private val dbHelper: DataBaseHelper) {
    private val TAG = "SemanaManager"

    fun insertarSemanasPrecargadas(db: SQLiteDatabase) {
        val semanas = listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")

        try {
            semanas.forEach { semana ->
                val values = ContentValues()
                values.put(DataBaseHelper.COLUMN_SEMANA_NOMBRE, semana)
                val resultado = db.insert(DataBaseHelper.TABLE_SEMANA, null, values)

                if (resultado == -1L) {
                    Log.e(TAG, "Error al insertar semana: $semana")
                } else {
                    Log.d(TAG, "Semana insertada correctamente: $semana con ID: $resultado")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al insertar semanas precargadas: ${e.message}")
            e.printStackTrace()
        }
    }

    fun obtenerTodasLasSemanas(): List<String> {
        val db = dbHelper.readableDatabase
        val semanas = mutableListOf<String>()

        try {
            val cursor = db.query(
                DataBaseHelper.TABLE_SEMANA,
                arrayOf(DataBaseHelper.COLUMN_SEMANA_NOMBRE),
                null, null, null, null, null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA_NOMBRE))
                        semanas.add(nombre)
                    } while (it.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener semanas: ${e.message}")
        }

        return semanas
    }
}