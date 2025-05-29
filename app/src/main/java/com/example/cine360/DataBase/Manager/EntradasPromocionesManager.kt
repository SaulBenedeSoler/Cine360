package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.EntradaPromociones

class EntradasPromocionesManager(private val dbHelper: DataBaseHelper) {

    /*Declaramos variables que mas adelante usaremos*/
    private val TABLE_NAME = DataBaseHelper.TABLE_ENTRADA_PROMOCIONES
    private val COLUMN_ID = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_ID
    private val COLUMN_USUARIO_ID = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_USUARIO_ID
    private val COLUMN_PROMOCION_ID = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_PROMOCION_ID
    private val COLUMN_NOMBRE_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_NOMBRE_PROMOCION
    private val COLUMN_DESCRIPCION_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_DESCRIPCION_PROMOCION
    private val COLUMN_PRECIO_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_PRECIO_PROMOCION
    private val COLUMN_IMAGEN_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_IMAGEN_PROMOCION

    /*Funcion para obtener las entradas por usuario*/
    fun obtenerEntradasPromocionesPorUsuario(usuarioId: Int): List<EntradaPromociones> {
        /*Variables de entradas para obtener una lista con todas las entradas*/
        val lista = mutableListOf<EntradaPromociones>()
        val db = dbHelper.readableDatabase

        /*Obtenemos todos los datos de la entrda*/
        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_USUARIO_ID,
            COLUMN_PROMOCION_ID,
            COLUMN_NOMBRE_PROMOCION,
            COLUMN_DESCRIPCION_PROMOCION,
            COLUMN_PRECIO_PROMOCION,
            COLUMN_IMAGEN_PROMOCION
        )
        /*Seleccionamos el id del usuario*/
        val selection = "$COLUMN_USUARIO_ID = ?"
        val selectionArgs = arrayOf(usuarioId.toString())
        /*Consultamos la tabla de entradas para obtener todas*/
        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        /*Iteramos sobre la entrada para obtener todas*/
        cursor?.use {
            while (it.moveToNext()) {
                val entradaPromocion = crearEntradaPromocionDesdeCursor(it)
                lista.add(entradaPromocion)
            }
        }

        db.close()
        return lista
    }

    /*Funcion para obtener todas las entradas de promociones*/
    fun obtenerTodasLasEntradasPromociones(): List<EntradaPromociones> {
        /*Cremos una lista con las entradas*/
        val lista = mutableListOf<EntradaPromociones>()
        /*Instanciamos ocn la base de datos*/
        val db = dbHelper.readableDatabase
        /*Obtenemos los datos*/
        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_USUARIO_ID,
            COLUMN_PROMOCION_ID,
            COLUMN_NOMBRE_PROMOCION,
            COLUMN_DESCRIPCION_PROMOCION,
            COLUMN_PRECIO_PROMOCION,
            COLUMN_IMAGEN_PROMOCION
        )
        /*Hacemos una consulta query para obtener todos los datos de latabla*/
        val cursor = db.query(
            TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        /*Llamamos a la funcion para crear entradas y lo añadimos a la lista*/
        cursor?.use {
            while (it.moveToNext()) {
                val entradaPromocion = crearEntradaPromocionDesdeCursor(it)
                lista.add(entradaPromocion)
            }
        }

        db.close()
        return lista
    }

    /*En esta funcion usamos el cursos para crear la entrad y lo hacemos mediante la llamada a las diferentes variables anteriormente delcarasdas*/
    private fun crearEntradaPromocionDesdeCursor(cursor: Cursor): EntradaPromociones {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ID))
        val promocionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROMOCION_ID))
        val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_PROMOCION))
        val precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECIO_PROMOCION))
        val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION_PROMOCION))
        val imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN_PROMOCION))
        return EntradaPromociones(id, usuarioId, promocionId, nombre, descripcion, precio,imagen )
    }

    /*Funcion para eliminar la entrada*/
    fun eliminarEntradaPromocion(id: Int): Int {
        /*Declaramos una instancia con la base de datos*/
        val db = dbHelper.writableDatabase
        /*Obtenemos el id de la entrada*/
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        /*Realizamos una consulta para borra entrada de la tabla de la entrada*/
        val deletedRows = db.delete(
            TABLE_NAME,
            selection,
            selectionArgs
        )

        db.close()
        return deletedRows
    }
    /*Funcion para crear entradas de promociones y llamamos a los datos que contiene esa tabla*/
    fun crearEntrada(
        usuarioId: Int,
        promocionId: Int,
        nombrePromocion: String,
        descripcionPromocion: String?,
        precioPromocion: Double,
        imagenPromocion: String?
    ): Long {
        /*Instanciamos la base de datos*/
        val db = dbHelper.writableDatabase
        /*Obtenemos los valores de la base de datos y lo asignamos a las variables anteriormente creadas*/
        val values = ContentValues().apply {
            put(COLUMN_USUARIO_ID, usuarioId)
            put(COLUMN_PROMOCION_ID, promocionId)
            put(COLUMN_NOMBRE_PROMOCION, nombrePromocion)
            put(COLUMN_DESCRIPCION_PROMOCION, descripcionPromocion)
            put(COLUMN_PRECIO_PROMOCION, precioPromocion)
            put(COLUMN_IMAGEN_PROMOCION, imagenPromocion)
        }
        /*Obtenemos el id de la tabla de entradas comida*/
        val newRowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return newRowId
    }
}
