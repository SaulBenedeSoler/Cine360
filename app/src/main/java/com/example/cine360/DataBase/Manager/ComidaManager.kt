package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Comida
import java.sql.SQLException

class ComidaManager(val dbHleper: DataBaseHelper) {

    private val TAG = "ComidaManager"



    fun insertarComida(db: SQLiteDatabase, comida: List<Comida>): List<Long> {
        val ids = mutableListOf<Long>()
        try {
            db.beginTransaction()
            comida.forEach { comida ->
                val values = ContentValues().apply {
                    put(DataBaseHelper.COLUMN_COMIDA_NOMBRE, comida.nombre)
                    put(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION, comida.descripcion)
                    put(DataBaseHelper.COLUMN_COMIDA_PRECIO, comida.precio)
                    put(DataBaseHelper.COLUMN_COMIDA_IMAGEN, comida.Imagen)
                }
                val id = db.insert(DataBaseHelper.TABLE_COMIDA, null, values)
                if (id == -1L) {
                    throw SQLException("Error al insertar promoción: ${comida.nombre}")
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

    fun insertarCOmidaPrecargada(db: SQLiteDatabase){

        val comida = listOf(

            Comida(1, "Menu Simple", "menusimple.png", "Contiene Palomitas y un refresco", 9.99),
            Comida(2, "Menu Mediano", "menumediano.png", "Contiene Palomitas y un refresco Mediano", 14.99),
            Comida(3, "Menu Grande", "menugrande.png", "Contiene palomitas y un refresco grande", 19.99),
            Comida(4, "Menu para dos", "menuparados.png", "Contiene dos cubos de palomitas y dos refrescos medianos", 24.99),
            Comida(5,"Menu nachos","menunachos.png", "Contiene nachos con salsas y un refresco", 9.99),
            Comida(6, "Menu Pizza", "menupizza.png", "Contiene una pizza y un refresco", 29.99),
            Comida(7, "Menu Dulce", "menuchocolate.png", "Contiene un surtido de chocolates", 4.99),
            Comida(8, "Menu para picar", "menudulce.png", "Contiene diferentes snacks y patatas a elegir", 14.99)

        )
        insertarComida(db, comida)
    }

    fun obtenerComidaconDb(db: SQLiteDatabase): List<Comida>{

        val Comida = mutableListOf<Comida>()
        val cursor = db.rawQuery("SELECT * FROM ${DataBaseHelper.TABLE_COMIDA}", null)
        cursor.use {

            if(it.moveToFirst()){

                do{
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))

                    val comida = Comida(id, nombre, descripcion, imagen, precio)
                    Comida.add(comida)
                }while(it.moveToNext())
            }

        }
        return Comida
    }

    fun ObtenerComidaPorId(comidaId: Int): Comida?{
        var comida: Comida? = null
        dbHleper.readableDatabase.use{db ->
            val cursor = db.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_COMIDA} WHERE ${DataBaseHelper.COLUMN_COMIDA_ID} = ?",
                arrayOf(comidaId.toString())
            )
            cursor.use {
                if (it.moveToFirst()){
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))

                    val comida = Comida(id, nombre, descripcion, imagen, precio)
                }
            }
        }
        return  comida
    }



}