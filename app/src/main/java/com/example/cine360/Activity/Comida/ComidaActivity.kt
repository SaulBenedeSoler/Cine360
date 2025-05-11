package com.example.cine360.Activity.Comida

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Adapter.ComidaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComidaActivity: AppCompatActivity() {

    private lateinit var recyclerviewComida: RecyclerView
    private lateinit var comidaAdapter: ComidaAdapter
    private lateinit var dbHelper: DataBaseHelper
    private var listaComida: MutableList<Comida> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comida)

        dbHelper = DataBaseHelper(applicationContext)

        recyclerviewComida = findViewById(R.id.recyclerViewComida)
        recyclerviewComida.layoutManager = LinearLayoutManager(this)
        listaComida = mutableListOf()
        comidaAdapter = ComidaAdapter(listaComida, this)
        recyclerviewComida.adapter = comidaAdapter

        cargarComida()
    }


    private fun cargarComida() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.readableDatabase
                val comidaList = obtenerComidaDesdeDB(db)
                db.close()
                withContext(Dispatchers.Main) {
                    listaComida.clear()
                    listaComida.addAll(comidaList)
                    comidaAdapter.actualizarComida(listaComida)
                }

            } catch (e: Exception) {
                Log.e("ComidaActivity", "Error al cargar comida: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComidaActivity, "Failed to load comida", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }


    private fun obtenerComidaDesdeDB(db: SQLiteDatabase): List<Comida> {
        val lista = mutableListOf<Comida>()
        try {
            val cursor = db.query(
                DataBaseHelper.TABLE_COMIDA,
                arrayOf(
                    DataBaseHelper.COLUMN_COMIDA_ID,
                    DataBaseHelper.COLUMN_COMIDA_NOMBRE,
                    DataBaseHelper.COLUMN_COMIDA_DESCRIPCION,
                    DataBaseHelper.COLUMN_COMIDA_PRECIO,
                    DataBaseHelper.COLUMN_COMIDA_IMAGEN
                ),
                null, null, null, null, null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_PRECIO))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))
                    val comida = Comida(id, nombre, imagen, descripcion, precio)
                    lista.add(comida)
                }

            }
        } catch (e: Exception) {
            Log.e("ComidaActivity", "Error al obtener comida de la BBDD: ${e.message}")
        }
        return lista
    }



}