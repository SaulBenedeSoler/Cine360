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

    /*Funcion para añadir promociones por parte del administrador
     * en la cual llamamos a los valores del archivo gestor de la base de datos y le pasamos los datos de la tabla
     * Para finalizar insertamos los datos en la tabla de promociones*/
    fun addPromocion(promocion: Promociones): Long {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promocion.nombre)
            put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promocion.descripcion)
            put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promocion.imagen)
            put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promocion.precio)
        }
        /*Insertamos una nueva fila en la base de datos dentro de la tabla promociones*/
        val newRowId = database.insert(DataBaseHelper.TABLE_PROMOCIONES, null, values)
        if (newRowId == -1L) {
            Log.e("AdminPromocionManager", "Error al insertar la promoción: ${promocion.nombre}")
        } else {
            Log.d("AdminPromocionManager", "Promoción insertada con ID: $newRowId")
        }
        return newRowId
    }

    /*Funcion para obtener todas las promociones*/
    fun getAllPromociones(): MutableList<Promociones> {
        /*Creamos una lista con la cual almacenaremos toodos los datos*/
        val promociones = mutableListOf<Promociones>()
        /*Llamamos a las instancias del archivo gesto de la base de datos*/
        val projection = arrayOf(
            DataBaseHelper.COLUMN_PROMOCION_ID,
            DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
            DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
            DataBaseHelper.COLUMN_PROMOCION_IMAGEN,
            DataBaseHelper.COLUMN_PROMOCION_PRECIO
        )
        /*Usamos un cursor para obtener los datos de la tabla promociones*/
        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_PROMOCIONES,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        /*Mediante el cursor obtenemos los datos del archivo gestor de la base de datos*/
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))
                val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))

                val promocion = Promociones(id, nombre, imagen, descripcion, precio)
                promociones.add(promocion)
            }
        }
        return promociones
    }

    /*Funcion para obtener las promociones por id*/
    fun getPromocionById(promocionId: Int): Promociones? {
        /*Creamos un array con los datos del archivo gestor de  base de daos*/
        val projection = arrayOf(
            DataBaseHelper.COLUMN_PROMOCION_ID,
            DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
            DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
            DataBaseHelper.COLUMN_PROMOCION_IMAGEN,
            DataBaseHelper.COLUMN_PROMOCION_PRECIO
        )
        /*Seleccionamos los datos mediante una consulta filtrada por el id de la promociones*/
        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        /*Mediante el cursor seleccionamos la tabla*/
        val selectionArgs = arrayOf(promocionId.toString())
        /*Mediante el cursor seleccionamos la tabla*/
        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_PROMOCIONES,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        /*Obtenemos los datos*/
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))
                val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))


                return Promociones(id, nombre, imagen, descripcion, precio)
            }
        }
        return null
    }

    /*Funcion para actualizar las promociones*/
    fun updatePromocion(promocion: Promociones): Int {
        /*Llamamos a los datos del archivo gestor de la base de datos y le pasamos los datos
      * de la tabla de la base de datos*/
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promocion.nombre)
            put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promocion.descripcion)
            put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promocion.imagen)
            put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promocion.precio)
        }
        /*Seleccionamos el contenido de la promocion mediante una busqueda
              * por id*/
        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        val selectionArgs = arrayOf(promocion.id.toString())
        /*Llamamos a la tabla promociones y actualizamos los datos en la base de datos*/
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

    /*Funcion para borrar promociones*/
    fun deletePromocion(promocionId: Int): Int {
        /*Seleccionamos el id de la promocion*/
        val selection = "${DataBaseHelper.COLUMN_PROMOCION_ID} = ?"
        val selectionArgs = arrayOf(promocionId.toString())
        /*Borramos los datos de esa pelicula llamando a la tabla promociones*/
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
