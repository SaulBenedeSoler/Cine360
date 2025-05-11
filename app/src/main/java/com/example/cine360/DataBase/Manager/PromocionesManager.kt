package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.ContactsContract.Data
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Actor
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.DataBase.Tablas.Director
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.DataBase.Tablas.Promociones
import java.sql.SQLException

class PromocionesManager(val dbHelper: DataBaseHelper) {

    private val TAG = "PromocionesManager"

    fun insertarPromociones(db: SQLiteDatabase, promociones: List<Promociones>): List<Long> {
        val ids = mutableListOf<Long>()
        try {
            db.beginTransaction()
            promociones.forEach { promocion ->
                val values = ContentValues().apply {
                    put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promocion.nombre)
                    put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promocion.descripcion)
                    put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promocion.precio)
                    put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promocion.imagen)
                }
                val id = db.insert(DataBaseHelper.TABLE_PROMOCIONES, null, values)
                if (id == -1L) {
                    throw SQLException("Error al insertar promoción: ${promocion.nombre}")
                }
                ids.add(id)
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.e(TAG, "Error al insertar promociones: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return ids
    }

    fun insertarPromocionesPrecargadas(db: SQLiteDatabase) {

        val promociones = listOf(

            /*Promociones Básicas*/
            Promociones(1, "Descubre la magia del cine", "descubrelamagia.png", "Te invitamos a obtener una gran experiencia con esta promoción descuentos en tus entradas para 3 películas ", 15.50),
            Promociones(2, "Tarjeta Regalo", "foto2.jpg", "Adquiere esta tarjeta con un valor de 30.99 en bar y cine.", 30.99),
            Promociones(3, "Cine en familia", "foto99.jpg", "Obten entradas gratis para toda la familia durante una semana", 49.99),
            Promociones(4, "Pase Mensual", "foto8983.jpg", "Compra esta promoción y obten entradas ilimitadas durante un mes", 59.99),
            Promociones(5, "Pase Anual", "aoda.dasda", "Compra esta promoción y obten entradas ilimitadas durante un año", 69.99),

            /*Promociones con Temática*/
            Promociones(6, "Noches de Insomnio", "foto3.jpg", "Obten esta promoción y recibiras el pack de supervivencia EXCLUSIVO que contiene palomitas, 2 refrescos y posters exclusivos", 20.50),
            Promociones(7, "No pares de reir", "foto4.jpg", "Obten un descuento del 10% en la entrada de la película a elegir, palomitas, 1 refresco y un juego de mesa con temática de humor", 19.99),
            Promociones(8, "Descarga de adrenalina", "foto5.jpg", "Obten con esta promoción dos entradas para cualquier película, un cubo grande de palomitas y dos refrescos pequeños", 15.99)

        )
        insertarPromociones(db, promociones)
    }


    fun obtenerPromocionporId(db: SQLiteDatabase): List<Comida>{

        val Comida = mutableListOf<Comida>()
        val cursor = db.rawQuery("SELECT * FROM ${DataBaseHelper.TABLE_COMIDA}", null)
        cursor.use {

            if(it.moveToFirst()){

                do{
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))
                    val comida = Comida(id, nombre, descripcion, imagen , precio)
                    Comida.add(comida)
                }while(it.moveToNext())
            }

        }
        return Comida
    }


}
