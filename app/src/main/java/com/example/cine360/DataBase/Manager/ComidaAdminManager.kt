package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.DataBase.Tablas.Promociones

class ComidaAdminManager (context: Context) {


    private val dbHelper = DataBaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase
    fun close() {
        dbHelper.close()
    }


    fun addComida(comida: Comida): Long {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_COMIDA_NOMBRE, comida.nombre)
            put(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION, comida.descripcion)
            put(DataBaseHelper.COLUMN_COMIDA_PRECIO, comida.precio)
            put(DataBaseHelper.COLUMN_COMIDA_IMAGEN, comida.Imagen)
        }

        val newRowId = database.insert(DataBaseHelper.TABLE_COMIDA, null, values)
        if (newRowId == -1L) {
            Log.e("AdminPromocionManager", "Error al insertar la promocion: ${comida.nombre}")
        } else {
            Log.d("AdminPeliculaManager", "Película insertada con ID: $newRowId")
        }
        return newRowId
    }


    fun getAllComida(): MutableList<Comida> {
        val comida = mutableListOf<Comida>()
        val projection = arrayOf(
            DataBaseHelper.COLUMN_COMIDA_ID,
            DataBaseHelper.COLUMN_COMIDA_NOMBRE,
            DataBaseHelper.COLUMN_COMIDA_PRECIO,
            DataBaseHelper.COLUMN_COMIDA_DESCRIPCION,
            DataBaseHelper.COLUMN_COMIDA_IMAGEN
        )

        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_COMIDA,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                val precio = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_PRECIO))
                val imagen = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))

                val comidas = Comida(id, nombre, descripcion,precio,imagen)
                comida.add(comidas)
            }
        }
        return comida
    }


    fun getComidaById(comidaId: Int): Comida? {
        val projection = arrayOf(
            DataBaseHelper.COLUMN_COMIDA_ID,
            DataBaseHelper.COLUMN_COMIDA_NOMBRE,
            DataBaseHelper.COLUMN_COMIDA_DESCRIPCION,
            DataBaseHelper.COLUMN_COMIDA_IMAGEN,
            DataBaseHelper.COLUMN_COMIDA_PRECIO,
        )

        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        val selectionArgs = arrayOf(comidaId.toString())

        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_COMIDA,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                val precio = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_PRECIO))
                val imagen = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))


                return Comida(id, nombre, descripcion,precio,imagen)
            }
        }
        return null
    }


    fun updateComida(comida: Comida): Int {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_COMIDA_NOMBRE, comida.nombre)
            put(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION, comida.descripcion)
            put(DataBaseHelper.COLUMN_COMIDA_IMAGEN, comida.Imagen)
            put(DataBaseHelper.COLUMN_COMIDA_PRECIO, comida.precio)
        }

        val selection = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
        val selectionArgs = arrayOf(comida.id.toString())

        val rowsAffected = database.update(
            DataBaseHelper.TABLE_COMIDA,
            values,
            selection,
            selectionArgs
        )

        if (rowsAffected > 0) {
            Log.d("ADMINCOMIDAMANAGER", "COMIDA con ID ${comida.id} actualizada.")
        } else {
            Log.e("ADMINCOMIDAMANAGER", "Error al actualizar la COMIDA con ID ${comida.id}.")
        }
        return rowsAffected
    }


    fun deleteComida(comidaId: Int): Int {
        val selection = "${DataBaseHelper.COLUMN_COMIDA_ID} = ?"
        val selectionArgs = arrayOf(comidaId.toString())

        val rowsDeleted = database.delete(
            DataBaseHelper.TABLE_COMIDA,
            selection,
            selectionArgs
        )

        if (rowsDeleted > 0) {
            Log.d("ADMINCOMIDAMANAGER", "COMIDA con ID $comidaId eliminada.")
        } else {
            Log.e("ADMINCOMIDAMANAGER", "Error al eliminar la COMIDA con ID $comidaId.")
        }
        return rowsDeleted
    }
}