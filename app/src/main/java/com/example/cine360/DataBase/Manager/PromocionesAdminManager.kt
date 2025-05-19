package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Promociones

class PromocionesAdminManager(context: Context) {

    private val dbHelper = DataBaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase


    fun close() {
        dbHelper.close()
    }


    fun addPromocion(promocion: Promociones): Long {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promocion.nombre)
            put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promocion.descripcion)
            put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promocion.imagen)
            put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promocion.precio)
        }

        val newRowId = database.insert(DataBaseHelper.TABLE_PROMOCIONES, null, values)
        if (newRowId == -1L) {
            Log.e("AdminPromocionManager", "Error al insertar la promoción: ${promocion.nombre}")
        } else {
            Log.d("AdminPromocionManager", "Promoción insertada con ID: $newRowId")
        }
        return newRowId
    }


    fun getAllPromociones(): MutableList<Promociones> {
        val promociones = mutableListOf<Promociones>()
        val projection = arrayOf(
            DataBaseHelper.COLUMN_PROMOCION_ID,
            DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
            DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
            DataBaseHelper.COLUMN_PROMOCION_IMAGEN,
            DataBaseHelper.COLUMN_PROMOCION_PRECIO
        )

        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_PROMOCIONES,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE)) // Corregido
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION)) // Corregido
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))   // Corregido
                val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))    // Corregido

                val promocion = Promociones(id, nombre, imagen, descripcion, precio) // Corregido el orden de los argumentos
                promociones.add(promocion)
            }
        }
        return promociones
    }


    fun getPromocionById(promocionId: Int): Promociones? {
        val projection = arrayOf(
            DataBaseHelper.COLUMN_PROMOCION_ID,
            DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
            DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
            DataBaseHelper.COLUMN_PROMOCION_IMAGEN,
            DataBaseHelper.COLUMN_PROMOCION_PRECIO
        )

        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        val selectionArgs = arrayOf(promocionId.toString())

        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_PROMOCIONES,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE)) // Corregido
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION)) // Corregido
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))   // Corregido
                val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))    // Corregido


                return Promociones(id, nombre, imagen, descripcion, precio)  // Corregido el orden
            }
        }
        return null
    }


    fun updatePromocion(promocion: Promociones): Int {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promocion.nombre)
            put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promocion.descripcion)
            put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promocion.imagen)
            put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promocion.precio)
        }

        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        val selectionArgs = arrayOf(promocion.id.toString())

        val rowsAffected = database.update(
            DataBaseHelper.TABLE_PROMOCIONES,
            values,
            selection,
            selectionArgs
        )

        if (rowsAffected > 0) {
            Log.d("AdminPromocionManager", "Promoción con ID ${promocion.id} actualizada.")
        } else {
            Log.e("AdminPromocionManager", "Error al actualizar la promoción con ID ${promocion.id}.")
        }
        return rowsAffected
    }


    fun deletePromocion(promocionId: Int): Int {
        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        val selectionArgs = arrayOf(promocionId.toString())

        val rowsDeleted = database.delete(
            DataBaseHelper.TABLE_PROMOCIONES,
            selection,
            selectionArgs
        )

        if (rowsDeleted > 0) {
            Log.d("AdminPromocionManager", "Promoción con ID $promocionId eliminada.")
        } else {
            Log.e("AdminPromocionManager", "Error al eliminar la promoción con ID $promocionId.")
        }
        return rowsDeleted
    }
}

