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


    fun addPromocion(promociones: Promociones): Long {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promociones.nombre)
            put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promociones.descripcion)
            put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promociones.precio)
            put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promociones.imagen)
        }

        val newRowId = database.insert(DataBaseHelper.TABLE_PELICULA, null, values)
        if (newRowId == -1L) {
            Log.e("AdminPromocionManager", "Error al insertar la promocion: ${promociones.nombre}")
        } else {
            Log.d("AdminPeliculaManager", "Película insertada con ID: $newRowId")
        }
        return newRowId
    }


    fun getAllPromociones(): MutableList<Promociones> {
        val promocion = mutableListOf<Promociones>()
        val projection = arrayOf(
            DataBaseHelper.COLUMN_PROMOCION_ID,
            DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
            DataBaseHelper.COLUMN_PROMOCION_PRECIO,
            DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
            DataBaseHelper.COLUMN_PROMOCION_IMAGEN
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
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                val precio = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))
                val imagen = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))

                val promociones = Promociones(id, nombre, descripcion,precio,imagen)
                promocion.add(promociones)
            }
        }
        return promocion
    }


    fun getPromocionById(promocionId: Int): Promociones? {
        val projection = arrayOf(
            DataBaseHelper.COLUMN_PROMOCION_ID,
            DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
            DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
            DataBaseHelper.COLUMN_PROMOCION_IMAGEN,
            DataBaseHelper.COLUMN_PROMOCION_PRECIO,
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
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                val precio = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))
                val imagen = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))


                return Promociones(id, nombre, descripcion,precio,imagen)
            }
        }
        return null
    }


    fun updatePromocion(promociones: Promociones): Int {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promociones.nombre)
            put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promociones.descripcion)
            put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promociones.imagen)
            put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promociones.precio)
        }

        val selection = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
        val selectionArgs = arrayOf(promociones.id.toString())

        val rowsAffected = database.update(
            DataBaseHelper.TABLE_PROMOCIONES,
            values,
            selection,
            selectionArgs
        )

        if (rowsAffected > 0) {
            Log.d("AdminPeliculaManager", "Película con ID ${promociones.id} actualizada.")
        } else {
            Log.e("AdminPeliculaManager", "Error al actualizar la película con ID ${promociones.id}.")
        }
        return rowsAffected
    }


    fun deletePromociones(promocionId: Int): Int {
        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        val selectionArgs = arrayOf(promocionId.toString())

        val rowsDeleted = database.delete(
            DataBaseHelper.TABLE_PROMOCIONES,
            selection,
            selectionArgs
        )

        if (rowsDeleted > 0) {
            Log.d("AdminPeliculaManager", "Película con ID $promocionId eliminada.")
        } else {
            Log.e("AdminPeliculaManager", "Error al eliminar la película con ID $promocionId.")
        }
        return rowsDeleted
    }
}