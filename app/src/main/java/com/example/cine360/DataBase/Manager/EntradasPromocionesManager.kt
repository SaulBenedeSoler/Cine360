package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.EntradaPromociones

class EntradasPromocionesManager(private val dbHelper: DataBaseHelper) {

    private val TABLE_NAME = DataBaseHelper.TABLE_ENTRADA_PROMOCIONES
    private val COLUMN_ID = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_ID
    private val COLUMN_USUARIO_ID = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_USUARIO_ID
    private val COLUMN_PROMOCION_ID = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_PROMOCION_ID
    private val COLUMN_NOMBRE_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_NOMBRE_PROMOCION
    private val COLUMN_DESCRIPCION_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_DESCRIPCION_PROMOCION
    private val COLUMN_PRECIO_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_PRECIO_PROMOCION
    private val COLUMN_IMAGEN_PROMOCION = DataBaseHelper.COLUMN_ENTRADA_PROMOCIONES_IMAGEN_PROMOCION

    fun crearEntradaPromocion(entradaPromocion: EntradaPromociones): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USUARIO_ID, entradaPromocion.usuarioId)
            put(COLUMN_PROMOCION_ID, entradaPromocion.promocionId)
            put(COLUMN_NOMBRE_PROMOCION, entradaPromocion.nombrePromocion)
            put(COLUMN_DESCRIPCION_PROMOCION, entradaPromocion.descripcionPromocion)
            put(COLUMN_PRECIO_PROMOCION, entradaPromocion.precioPromocion)
            put(COLUMN_IMAGEN_PROMOCION, entradaPromocion.imagenPromocion)
        }

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun obtenerEntradasPromocionesPorUsuario(usuarioId: Int): List<EntradaPromociones> {
        val lista = mutableListOf<EntradaPromociones>()
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_USUARIO_ID,
            COLUMN_PROMOCION_ID,
            COLUMN_NOMBRE_PROMOCION,
            COLUMN_DESCRIPCION_PROMOCION,
            COLUMN_PRECIO_PROMOCION,
            COLUMN_IMAGEN_PROMOCION
        )
        val selection = "$COLUMN_USUARIO_ID = ?"
        val selectionArgs = arrayOf(usuarioId.toString())

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val entradaPromocion = crearEntradaPromocionDesdeCursor(it)
                lista.add(entradaPromocion)
            }
        }

        db.close()
        return lista
    }

    fun obtenerTodasLasEntradasPromociones(): List<EntradaPromociones> {
        val lista = mutableListOf<EntradaPromociones>()
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_USUARIO_ID,
            COLUMN_PROMOCION_ID,
            COLUMN_NOMBRE_PROMOCION,
            COLUMN_DESCRIPCION_PROMOCION,
            COLUMN_PRECIO_PROMOCION,
            COLUMN_IMAGEN_PROMOCION
        )

        val cursor = db.query(
            TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val entradaPromocion = crearEntradaPromocionDesdeCursor(it)
                lista.add(entradaPromocion)
            }
        }

        db.close()
        return lista
    }

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

    fun eliminarEntradaPromocion(id: Int): Int {
        val db = dbHelper.writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        val deletedRows = db.delete(
            TABLE_NAME,
            selection,
            selectionArgs
        )

        db.close()
        return deletedRows
    }

    fun crearEntrada(
        usuarioId: Int,
        promocionId: Int,
        nombrePromocion: String,
        descripcionPromocion: String?,
        precioPromocion: Double,
        imagenPromocion: String?
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USUARIO_ID, usuarioId)
            put(COLUMN_PROMOCION_ID, promocionId)
            put(COLUMN_NOMBRE_PROMOCION, nombrePromocion)
            put(COLUMN_DESCRIPCION_PROMOCION, descripcionPromocion)
            put(COLUMN_PRECIO_PROMOCION, precioPromocion)
            put(COLUMN_IMAGEN_PROMOCION, imagenPromocion)
        }
        val newRowId = db.insert(TABLE_NAME, null, values)
        db.close()
        return newRowId
    }
}

