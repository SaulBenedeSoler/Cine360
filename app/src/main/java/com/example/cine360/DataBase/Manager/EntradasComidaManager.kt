package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.EntradaComida
import com.example.cine360.DataBase.Tablas.EntradaPromociones

class EntradasComidaManager(private val dbHelper: DataBaseHelper) {

    private val TABLE_ENTRADA_COMIDA = "entrada_comida"
    private val COLUMN_ENTRADA_ID = "id"
    private val COLUMN_ENTRADA_COMIDA_USUARIO_ID = "usuarioId"
    private val COLUMN_ENTRADA_COMIDA_COMIDA_ID = "comidaId"
    private val COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA = "nombreComida"
    private val COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA = "descripcionComida"
    private val COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA = "precioComida"
    private val COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA = "imagenComida"

    fun crearEntradaComida(
        usuarioId: Int,
        comidaId: Int,
        nombreComida: String,
        descripcionComida: String?,
        precioComida: Double,
        imagenComida: String?
    ): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_ENTRADA_COMIDA_USUARIO_ID, usuarioId)
            put(COLUMN_ENTRADA_COMIDA_COMIDA_ID, comidaId)
            put(COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA, nombreComida)
            put(COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA, descripcionComida)
            put(COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA, precioComida)
            put(COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA, imagenComida)
        }

        val id = db.insert(TABLE_ENTRADA_COMIDA, null, values)
        Log.d("EntradasComidaManager", "Nueva entrada de comida creada con ID: $id")
        return id
    }

    fun obtenerTodasLasEntradasComida(): List<EntradaComida> {
        val entradasComida = mutableListOf<EntradaComida>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            TABLE_ENTRADA_COMIDA,
            null,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val entradaComida = cursorToEntradaComida(cursor)
            entradasComida.add(entradaComida)
        }

        cursor.close()
        return entradasComida
    }

    fun obtenerEntradasComidaPorUsuario(usuarioId: Int): List<EntradaComida> {
        val entradasComida = mutableListOf<EntradaComida>()
        val db = dbHelper.readableDatabase

        val selection = "${COLUMN_ENTRADA_COMIDA_USUARIO_ID} = ?"
        val selectionArgs = arrayOf(usuarioId.toString())

        val cursor = db.query(
            TABLE_ENTRADA_COMIDA,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val entradaComida = cursorToEntradaComida(cursor)
            entradasComida.add(entradaComida)
        }

        cursor.close()
        return entradasComida
    }

    private fun cursorToEntradaComida(cursor: Cursor): EntradaComida {
        val idIndex = cursor.getColumnIndex(COLUMN_ENTRADA_ID)
        val usuarioIdIndex = cursor.getColumnIndex(COLUMN_ENTRADA_COMIDA_USUARIO_ID)
        val comidaIdIndex = cursor.getColumnIndex(COLUMN_ENTRADA_COMIDA_COMIDA_ID)
        val nombrecomidaIndex = cursor.getColumnIndex(COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA)
        val descripcioncomidaIndex = cursor.getColumnIndex(COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA)
        val preciocomidaIndex = cursor.getColumnIndex(COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA)
        val imagencomidaIndex = cursor.getColumnIndex(COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA)

        val id = if (idIndex >= 0) cursor.getInt(idIndex) else 0
        val usuarioId = if (usuarioIdIndex >= 0) cursor.getInt(usuarioIdIndex) else 0
        val comidaId = if (comidaIdIndex >= 0) cursor.getInt(comidaIdIndex) else 0
        val nombreComida = if (nombrecomidaIndex >= 0) cursor.getString(nombrecomidaIndex) else ""
        val descripcionComida = if (descripcioncomidaIndex >= 0) cursor.getString(descripcioncomidaIndex) else null
        val precioComida = if (preciocomidaIndex >= 0) cursor.getDouble(preciocomidaIndex) else 0.0
        val imagenComida = if (imagencomidaIndex >= 0) cursor.getString(imagencomidaIndex) else null

        return EntradaComida(
            id = id,
            usuarioId = usuarioId,
            comidaId = comidaId,
            nombrecomida = nombreComida,
            descripcioncomida = descripcionComida,
            preciocomida = precioComida,
            imagencomida = imagenComida
        )
    }

    fun eliminarEntradaComida(entradacomidaId: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val selection = "${COLUMN_ENTRADA_ID} = ?"
            val selectionArgs = arrayOf(entradacomidaId.toString())

            val deletedRows = db.delete(TABLE_ENTRADA_COMIDA, selection, selectionArgs)
            Log.d("EntradasComidaManager", "Filas de Comida eliminadas: $deletedRows")
            db.setTransactionSuccessful()
            return deletedRows > 0
        } catch (e: Exception) {
            Log.e("EntradasComidaManager", "Error al eliminar entrada de comida: ${e.message}")
            return false
        } finally {
            db.endTransaction()
        }
    }
}