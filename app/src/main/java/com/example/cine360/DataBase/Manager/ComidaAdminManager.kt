package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Comida

class ComidaAdminManager(context: Context) {

    /*Declaracion de variables y creacion de instancia para comunicrse con la base d edatos*/
    private val dbHelper = DataBaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase

    /*Funcion para cerrar conexion con la base de datos*/
    fun close() {
        dbHelper.close()
    }
    /*Funcion para añadir comida*/
    fun addComida(comida: Comida): Long {
        /*Llamamos a los daos del archivo base de datos y a los datos de la tabla de comida*/
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_COMIDA_NOMBRE, comida.nombre)
            put(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION, comida.descripcion)
            put(DataBaseHelper.COLUMN_COMIDA_PRECIO, comida.precio)
            put(DataBaseHelper.COLUMN_COMIDA_IMAGEN, comida.Imagen)
        }
        /*Indicamos que inserte una nueva fila llamando a la tabla comida*/
        val newRowId = database.insert(DataBaseHelper.TABLE_COMIDA, null, values)
        if (newRowId == -1L) {
            Log.e("ComidaAdminManager", "Error al insertar la comida: ${comida.nombre}")
        } else {
            Log.d("ComidaAdminManager", "Comida insertada con ID: $newRowId")
        }
        return newRowId
    }

    /*Funcion para obtener toda la comida mediante la llamada
    * a los datos del archivo DBHleper*/
    fun getAllComida(): MutableList<Comida> {
        val comidaList = mutableListOf<Comida>()
        val projection = arrayOf(
            DataBaseHelper.COLUMN_COMIDA_ID,
            DataBaseHelper.COLUMN_COMIDA_NOMBRE,
            DataBaseHelper.COLUMN_COMIDA_DESCRIPCION,
            DataBaseHelper.COLUMN_COMIDA_PRECIO,
            DataBaseHelper.COLUMN_COMIDA_IMAGEN
        )
        /*Llamamiento a la base d edatos*/
        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_COMIDA,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        /*Llamamos a los difernetes datos del archivo gestor de base de datos*/
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_PRECIO))
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))
                /*Llamamos a la tabla y le pasamos los datos necesarios para insertar y después pasamos esa lista para añadirla*/
                val comida = Comida(id, nombre, imagen, descripcion, precio)
                comidaList.add(comida)
            }
        }
        return comidaList
    }

    /*FUncion para obtenerla comida por id mediante el llamamiento a la base de datos
    * y obtencion de datos de esto mediante el uso del id*/
    fun getComidaById(comidaId: Int): Comida? {
        val projection = arrayOf(
            DataBaseHelper.COLUMN_COMIDA_ID,
            DataBaseHelper.COLUMN_COMIDA_NOMBRE,
            DataBaseHelper.COLUMN_COMIDA_DESCRIPCION,
            DataBaseHelper.COLUMN_COMIDA_IMAGEN,
            DataBaseHelper.COLUMN_COMIDA_PRECIO
        )

        val selection = "${DataBaseHelper.COLUMN_COMIDA_ID} = ?"
        val selectionArgs = arrayOf(comidaId.toString())
        /*Hago una consulta a la tabla de la base de datos*/
        val cursor: Cursor = database.query(
            DataBaseHelper.TABLE_COMIDA,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        /*Indicamos que coga desde el primer menu y obteniendo todos los datos de la tabla*/
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID)).toInt()
                val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))
                val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_PRECIO))

                return Comida(id, nombre, imagen, descripcion, precio)
            }
        }
        return null
    }

    /*Funcion para actualizar la comida y llamaos a los datos del archivo gestor de la base de datos
    * y a los datos de la tabla de la base de datos*/
    fun updateComida(comida: Comida): Int {
        val values = ContentValues().apply {
            put(DataBaseHelper.COLUMN_COMIDA_NOMBRE, comida.nombre)
            put(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION, comida.descripcion)
            put(DataBaseHelper.COLUMN_COMIDA_IMAGEN, comida.Imagen)
            put(DataBaseHelper.COLUMN_COMIDA_PRECIO, comida.precio)
        }

        /*Realizamos la obtencion del id de ka comida y obtenemos  los datos de la tabla*/
        val selection = "${DataBaseHelper.COLUMN_COMIDA_ID} = ?"
        val selectionArgs = arrayOf(comida.id.toString())

        val rowsAffected = database.update(
            DataBaseHelper.TABLE_COMIDA,
            values,
            selection,
            selectionArgs
        )

        if (rowsAffected > 0) {
            Log.d("ComidaAdminManager", "Comida con ID ${comida.id} actualizada.")
        } else {
            Log.e("ComidaAdminManager", "Error al actualizar la Comida con ID ${comida.id}.")
        }
        return rowsAffected
    }

    /*Funcion para eliminar la comida*/
    fun deleteComida(comidaId: Int): Int {
        /*Obtenemos el id de la comida*/
        val selection = "${DataBaseHelper.COLUMN_COMIDA_ID} = ?"
        val selectionArgs = arrayOf(comidaId.toString())
        /*Indicamos que borre la fila de la tabla*/
        val rowsDeleted = database.delete(
            DataBaseHelper.TABLE_COMIDA,
            selection,
            selectionArgs
        )

        if (rowsDeleted > 0) {
            Log.d("ComidaAdminManager", "Comida con ID $comidaId eliminada.")
        } else {
            Log.e("ComidaAdminManager", "Error al eliminar la Comida con ID $comidaId.")
        }
        return rowsDeleted
    }
}