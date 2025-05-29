package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Comida
import java.sql.SQLException

class ComidaManager(val dbHleper: DataBaseHelper) {

    private val TAG = "ComidaManager"

    /*Funcion para insertar la comida*/
    fun insertarComida(db: SQLiteDatabase, comida: List<Comida>): List<Long> {
        val ids = mutableListOf<Long>()
        try {
            /*Obtenemos los datos del archivo gestor de la base de datos y los datos de la tabla de la base de datos*/
            db.beginTransaction()
            comida.forEach { comida ->
                val values = ContentValues().apply {
                    put(DataBaseHelper.COLUMN_COMIDA_NOMBRE, comida.nombre)
                    put(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION, comida.descripcion)
                    put(DataBaseHelper.COLUMN_COMIDA_PRECIO, comida.precio)
                    put(DataBaseHelper.COLUMN_COMIDA_IMAGEN, comida.Imagen)
                }
                /*Obtenemos el id de la comida para saber que comida queremos insertar*/
                val id = db.insert(DataBaseHelper.TABLE_COMIDA, null, values)
                if (id == -1L) {
                    throw SQLException("Error al insertar comida: ${comida.nombre}")
                }
                ids.add(id)
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.e(TAG, "Error al insertar Comida: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return ids
    }
    /*Funcion para insertar la comida*/
    fun insertarCOmidaPrecargada(db: SQLiteDatabase){

        /*Insetar la comida en la base de datos*/
        val comida = listOf(

            Comida(1, "Menú Simple", "menusimple.png", "Contiene palomitas y un refresco.", 9.99),
            Comida(2, "Menú Mediano", "menumediano.png", "Contiene palomitas y un refresco mediano.", 14.99),
            Comida(3, "Menú Grande", "menugrande.png", "Contiene palomitas y un refresco grande.", 19.99),
            Comida(4, "Menú para dos", "menuparados.png", "Contiene dos cubos de palomitas y dos refrescos medianos.", 24.99),
            Comida(5,"Menú nachos","menunachos.png", "Contiene nachos con salsas y un refresco.", 9.99),
            Comida(6, "Menú Pizza", "menupizza.png", "Contiene una pizza y un refresco.", 29.99),
            Comida(7, "Menú Dulce", "menuchocolate.png", "Contiene un surtido de chocolates.", 4.99),
            Comida(8, "Menú para picar", "menudulce.png", "Contiene diferentes snacks y patatas a elegir.", 14.99)

        )
        insertarComida(db, comida)
    }



}