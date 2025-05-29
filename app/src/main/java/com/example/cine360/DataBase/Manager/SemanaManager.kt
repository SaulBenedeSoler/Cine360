package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper

class SemanaManager(private val dbHelper: DataBaseHelper) {
    private val TAG = "SemanaManager"


    /*Funcion para obtener todas las semanas de la base d edatos*/
    fun obtenerTodasLasSemanas(): List<String> {
        val db = dbHelper.readableDatabase
        val semanas = mutableListOf<String>()
        /*Realizamos una ocnsulta query para obtener el nombre de cada semana*/
        try {
            val cursor = db.query(
                DataBaseHelper.TABLE_SEMANA,
                arrayOf(DataBaseHelper.COLUMN_SEMANA_NOMBRE),
                null, null, null, null, null
            )
            /*Cogemos l nombre de cada una y lo añadimos al objeto semanas el cual esta unido a la base de datos*/
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