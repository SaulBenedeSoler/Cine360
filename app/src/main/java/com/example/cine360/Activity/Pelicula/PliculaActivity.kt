package com.example.cine360.Activity.Pelicula

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView // Import ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Entrada.EntradaActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.Adapter.PeliculaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class PeliculaActivity : AppCompatActivity() {

    private lateinit var peliculaManager: PeliculaManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAjustes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peliculas)


        val semanaSeleccionada = intent.getStringExtra("SEMANA") ?: "Semana desconocida"
        Log.d("PeliculaActivity", "Semana seleccionada: $semanaSeleccionada")


        val dbHelper = DataBaseHelper(this)
        val db = dbHelper.readableDatabase
        peliculaManager = PeliculaManager(dbHelper)


        recyclerView = findViewById(R.id.recyclerViewPeliculas)


        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        imageAjustes = findViewById(R.id.imageAjustes)
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }


        val semanaId = obtenerIdSemana(semanaSeleccionada, db)
        Log.d("PeliculaActivity", "Semana ID: $semanaId")


        val peliculas = obtenerPeliculasPorSemana(db, semanaId)


        Log.d("PeliculaActivity", "Películas encontradas: ${peliculas.size}")


        val adapter = PeliculaAdapter(this, peliculas)
        recyclerView.adapter = adapter
    }

    private fun obtenerPeliculasPorSemana(db: SQLiteDatabase, semanaId: Int): List<Pelicula> {
        val peliculas = mutableListOf<Pelicula>()

        try {

            val semanaString = (semanaId + 1).toString()

            val query = "SELECT * FROM ${DataBaseHelper.TABLE_PELICULA} WHERE ${DataBaseHelper.COLUMN_SEMANA} = ?"
            db.rawQuery(query, arrayOf(semanaString)).use { cursor ->
                Log.d("PeliculaActivity", "Cursor count: ${cursor.count}")

                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_Pelicula_ID))
                        val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUM_TITULO))
                        val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPCION))
                        val genero = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_GENERO))
                        val fechaLanzamiento = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO))
                        val duracion = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DURACION))
                        val imagen = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_IMAGEN))
                        val trailer = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_TRAILER))
                        val semana = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA))

                        val pelicula = Pelicula(id.toInt(), titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana)
                        peliculas.add(pelicula)

                        Log.d("PeliculaActivity", "Película añadida: $titulo")
                    } while (cursor.moveToNext())
                } else {
                    Log.d("PeliculaActivity", "No se encontraron películas para esta semana")
                }
            }
        } catch (e: Exception) {
            Log.e("PeliculaActivity", "Error al obtener películas por semana: ${e.message}")
        }

        return peliculas
    }



    private fun obtenerIdSemana(nombreSemana: String, db: SQLiteDatabase): Int {
        val semanaRegex = "Semana (\\d+)".toRegex()
        val matchResult = semanaRegex.find(nombreSemana)

        return if (matchResult != null) {
            val numero = matchResult.groupValues[1].toIntOrNull() ?: 1
            numero - 1
        } else {
            0
        }
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
