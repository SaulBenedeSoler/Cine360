package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.EntradaComida
import com.example.cine360.DataBase.Tablas.EntradaPromociones

class EntradasComidaManager(private val dbHelper: DataBaseHelper) {

    /*Declaramos variables que mas adelante usaremos*/
    private val TABLE_ENTRADA_COMIDA = "entrada_comida"
    private val COLUMN_ENTRADA_ID = "id"
    private val COLUMN_ENTRADA_COMIDA_USUARIO_ID = "usuarioId"
    private val COLUMN_ENTRADA_COMIDA_COMIDA_ID = "comidaId"
    private val COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA = "nombreComida"
    private val COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA = "descripcionComida"
    private val COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA = "precioComida"
    private val COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA = "imagenComida"

    /*Funcion para crear entradas de comida y llamamos a los datos que contiene esa tabla*/
    fun crearEntradaComida(
        usuarioId: Int,
        comidaId: Int,
        nombreComida: String,
        descripcionComida: String?,
        precioComida: Double,
        imagenComida: String?
    ): Long {
        /*Instanciamos la base de datos*/
        val db = dbHelper.writableDatabase
        /*Obtenemos los valores de la base de datos y lo asignamos a las variables anteriormente creadas*/
        val values = ContentValues().apply {
            put(COLUMN_ENTRADA_COMIDA_USUARIO_ID, usuarioId)
            put(COLUMN_ENTRADA_COMIDA_COMIDA_ID, comidaId)
            put(COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA, nombreComida)
            put(COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA, descripcionComida)
            put(COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA, precioComida)
            put(COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA, imagenComida)
        }
        /*Obtenemos el id de la tabla de entradas comida*/
        val id = db.insert(TABLE_ENTRADA_COMIDA, null, values)
        return id
    }


    /*Funcion para obtener las entradas por usuario*/
    fun obtenerEntradasComidaPorUsuario(usuarioId: Int): List<EntradaComida> {
        /*Variables de entradas para obtener una lista con todas las entradas*/
        val entradasComida = mutableListOf<EntradaComida>()

        val db = dbHelper.readableDatabase
        /*Seleccionamos el id del usuario*/
        val selection = "${COLUMN_ENTRADA_COMIDA_USUARIO_ID} = ?"
        /*Instnaciamso la base de datos*/
        val selectionArgs = arrayOf(usuarioId.toString())
        /*Consultamos la tabla de entradas para obtener todas*/
        val cursor = db.query(
            TABLE_ENTRADA_COMIDA,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        /*Iteramos sobre la entrada para obtener todas*/
        while (cursor.moveToNext()) {
            val entradaComida = cursorToEntradaComida(cursor)
            entradasComida.add(entradaComida)
        }

        cursor.close()
        return entradasComida
    }
    /*Funcion usada para llamar a los datos del archivo gestor de la base de datos y obtener la entrada*/
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

        /*Devolvemos la entrada con todos lso datos ya registrados*/
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
    /*Funcion para eliminar la entrada*/
    fun eliminarEntradaComida(entradacomidaId: Int): Boolean {
        /*Declaramos una instancia con la base de datos*/
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            /*Obtenemos el id de la entrada*/
            val selection = "${COLUMN_ENTRADA_ID} = ?"
            val selectionArgs = arrayOf(entradacomidaId.toString())
            /*Realizamos una consulta para borra entrada de la tabla de la entrada*/
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