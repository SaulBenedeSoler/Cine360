package com.example.cine360.Activity.Promociones

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PromocionesActivity : AppCompatActivity() {

    private lateinit var recyclerViewPromociones: RecyclerView
    private lateinit var promocionesAdapter: PromocionesAdapter
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var entradasPromocionesManager: EntradasPromocionesManager
    private var listaPromociones: List<Promociones> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promocion)


        dbHelper = DataBaseHelper(applicationContext)
        entradasPromocionesManager = EntradasPromocionesManager(dbHelper)


        recyclerViewPromociones = findViewById(R.id.recyclerViewPromociones)
        recyclerViewPromociones.layoutManager = LinearLayoutManager(this)


        promocionesAdapter = PromocionesAdapter(emptyList(), this)
        recyclerViewPromociones.adapter = promocionesAdapter


        cargarPromociones()
    }

    private fun cargarPromociones() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.readableDatabase
                listaPromociones = obtenerPromocionesDesdeDB(db)
                db.close()
                withContext(Dispatchers.Main) {
                    promocionesAdapter.actualizarPromociones(listaPromociones)
                }
            } catch (e: Exception) {
                Log.e("PromocionesActivity", "Error al cargar promociones: ${e.message}")
                withContext(Dispatchers.Main) {

                    Toast.makeText(this@PromocionesActivity, "Failed to load promotions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun obtenerPromocionesDesdeDB(db: SQLiteDatabase): List<Promociones> {
        val lista = mutableListOf<Promociones>()
        try {
            val cursor = db.query(
                DataBaseHelper.TABLE_PROMOCIONES,
                arrayOf(
                    DataBaseHelper.COLUMN_PROMOCION_ID,
                    DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
                    DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
                    DataBaseHelper.COLUMN_PROMOCION_IMAGEN,
                    DataBaseHelper.COLUMN_PROMOCION_PRECIO
                ),
                null, null, null, null, null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))
                    val promocion = Promociones(id, nombre, descripcion, imagen, precio)
                    lista.add(promocion)
                }
            }
        } catch (e: Exception) {
            Log.e("PromocionesActivity", "Error al obtener promociones de la BBDD: ${e.message}")
        }
        return lista
    }

    private fun obtenerIdUsuarioActual(): Int {

        Log.d("PromocionesActivity", "obtenerIdUsuarioActual() called")
        return 123
    }
}
