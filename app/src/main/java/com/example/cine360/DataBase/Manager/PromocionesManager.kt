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
    /*Funcion para insertar promociones en la base de datos*/
    fun insertarPromociones(db: SQLiteDatabase, promociones: List<Promociones>): List<Long> {
        /*Creamos una variable que contendra una lista con el id de las promociones*/
        val ids = mutableListOf<Long>()
        try {
            /*Creamos una instancia con la base de datos*/
            db.beginTransaction()
            /*Usamos un for each para iterar sobre la lista de peliculas*/
            promociones.forEach { promocion ->
                /*Creamos un objeto content values con el cual almacenamos los valores que se van a insertar en
             * una fila de la base de datos llamando a los nombres de las columnas con sus valores*/
                val values = ContentValues().apply {
                    put(DataBaseHelper.COLUMN_PROMOCION_NOMBRE, promocion.nombre)
                    put(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION, promocion.descripcion)
                    put(DataBaseHelper.COLUMN_PROMOCION_PRECIO, promocion.precio)
                    put(DataBaseHelper.COLUMN_PROMOCION_IMAGEN, promocion.imagen)
                }
                /*Llamamos a la base de datos y insertamos en la base de datos los valores en la tabla promociones*/
                val id = db.insert(DataBaseHelper.TABLE_PROMOCIONES, null, values)
                /*Si el id no existe mostramos un error*/
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
    /*Funcion apra insertar las promociones en la base de datos a la hora de actualizar o iniciar de 0 la base de datos*/
    fun insertarPromocionesPrecargadas(db: SQLiteDatabase) {

        val promociones = listOf(

            /*Promociones Básicas*/
            Promociones(1, "Descubre la magia del cine", "descubrelamagia.png", "Te invitamos a obtener una gran experiencia con esta promoción: descuentos en tus entradas para 3 películas.", 15.50),
            Promociones(2, "Tarjeta Regalo", "tarjetaregalo.png", "Adquiere esta tarjeta con un valor de 30.99 en bar y cine.", 30.99),
            Promociones(3, "Cine en familia", "cineenfamilia.png", "Obtén entradas gratis para toda la familia durante una semana.", 49.99),
            Promociones(4, "Pase Mensual", "pasemensual.png", "Compra esta promoción y obtén entradas ilimitadas durante un mes.", 59.99),
            Promociones(5, "Pase Anual", "paseanual.png", "Compra esta promoción y obtén entradas ilimitadas durante un año.", 69.99),

            /*Promociones con Temática*/
            Promociones(6, "Noches de Insomnio", "nochesdeinsomnio.png", "Obtén esta promoción y recibirás el pack de supervivencia EXCLUSIVO que contiene palomitas, 2 refrescos y pósteres exclusivos.", 20.50),
            Promociones(7, "No pares de reír", "noparesdereir.png", "Obtén un descuento del 10% en la entrada de la película a elegir, palomitas, 1 refresco y un juego de mesa con temática de humor.", 19.99),
            Promociones(8, "Descarga de adrenalina", "descargaadrenalina.png", "Obtén con esta promoción dos entradas para cualquier película, un cubo grande de palomitas y dos refrescos pequeños.", 15.99)

        )
        insertarPromociones(db, promociones)
    }

    /*Funcion para obtener las promociones por id*/
    fun obtenerPromocionporId(db: SQLiteDatabase): List<Promociones>{

        val Promocion = mutableListOf<Promociones>()
        /*Llamamos a la base de datos y hacemos una consulta indicando que obtenga todos los datos
           * de la tabla promociones por id*/
        val cursor = db.rawQuery("SELECT * FROM ${DataBaseHelper.TABLE_PROMOCIONES}", null)
        cursor.use {
            /*Indicamos que coga los datos del archivo gestor de base de datos*/
            if(it.moveToFirst()){

                do{
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))
                    val promocion = Promociones(id, nombre, descripcion, imagen , precio)
                    Promocion.add(promocion)
                }while(it.moveToNext())
            }

        }
        return Promocion
    }


}