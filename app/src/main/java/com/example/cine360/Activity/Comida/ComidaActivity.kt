package com.example.cine360.Activity.Comida

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
import com.example.cine360.Activity.Entrada.EntradaActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
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
    private lateinit var imageAjustes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comida)

        dbHelper = DataBaseHelper(applicationContext)

        recyclerviewComida = findViewById(R.id.recyclerViewComida)
        recyclerviewComida.layoutManager = LinearLayoutManager(this)
        listaComida = mutableListOf()
        comidaAdapter = ComidaAdapter(listaComida, this)
        recyclerviewComida.adapter = comidaAdapter;
        imageAjustes = findViewById(R.id.imageAjustes)


        cargarComida()

        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }

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
                    startActivity(Intent(this, PeliculaActivity::class.java))
                    true
                }
                R.id.menu_comida -> {
                    startActivity(Intent(this, Comida::class.java))
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
