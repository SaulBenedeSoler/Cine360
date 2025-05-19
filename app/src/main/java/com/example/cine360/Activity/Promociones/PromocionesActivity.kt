package com.example.cine360.Activity.Promociones

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Entrada.EntradaActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.Adapter.PromocionesAdapter
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
    private var listaPromociones: MutableList<Promociones> = mutableListOf()
    private lateinit var imageAjustes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promocion)


        dbHelper = DataBaseHelper(applicationContext)
        entradasPromocionesManager = EntradasPromocionesManager(dbHelper)


        recyclerViewPromociones = findViewById(R.id.recyclerViewPromociones)
        recyclerViewPromociones.layoutManager = LinearLayoutManager(this)


        promocionesAdapter = PromocionesAdapter(emptyList(), this)
        recyclerViewPromociones.adapter = promocionesAdapter

        imageAjustes = findViewById(R.id.imageAjustes)
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }

        cargarPromociones()
    }

    private fun cargarPromociones() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.readableDatabase
                val comidaList = obtenerPromocionesDesdeDB(db)
                db.close()
                withContext(Dispatchers.Main) {
                    listaPromociones.clear()
                    listaPromociones.addAll(comidaList)
                    promocionesAdapter.actualizarPromociones(listaPromociones)
                }

            } catch (e: Exception) {
                Log.e("ComidaActivity", "Error al cargar comida: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PromocionesActivity, "Failed to load comida", Toast.LENGTH_SHORT).show()
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
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))
                    val promocion = Promociones(id, nombre, imagen, descripcion , precio)
                    lista.add(promocion)
                }
            }
        } catch (e: Exception) {
            Log.e("PromocionesActivity", "Error al obtener promociones de la BBDD: ${e.message}")
        }
        return lista
    }



    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_promociones -> {
                    startActivity(Intent(this, PromocionesActivity::class.java))
                    true
                }
                R.id.menu_peliculas -> {
                    startActivity(Intent(this, SemanaActivity::class.java))
                    true
                }
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java))
                    true
                }

                R.id.menu_entradas_comida -> {
                    startActivity(Intent(this, EntradaComidaActivity::class.java))
                    true
                }
                R.id.menu_entradas_promociones -> {
                    startActivity(Intent(this, EntradaPromocionActivity::class.java))
                    true
                }
                R.id.menu_entradas_peliculas -> {
                    startActivity(Intent(this, EntradaActivity::class.java))
                    true
                }
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java))
                    true
                }

                R.id.menu_usuario -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }

                R.id.menu_cerrar_sesion -> {
                    LoginActivity.cerrarSesion(this)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

}
