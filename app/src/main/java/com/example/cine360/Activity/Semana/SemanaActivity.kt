package com.example.cine360.Activity.Semana

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
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
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Adapter.SemanaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Manager.SemanaManager
import com.example.cine360.R

class SemanaActivity : AppCompatActivity() {

    private lateinit var imageAjustes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semanas)

        val dbHelper = DataBaseHelper(this)
        val db = dbHelper.readableDatabase
        val semanaManager = SemanaManager(dbHelper)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSemanas)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val semanas = semanaManager.obtenerTodasLasSemanas()


        val semanasParaMostrar = if (semanas.isEmpty()) {
            listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")
        } else {
            semanas
        }


        val peliculaManager = PeliculaManager(dbHelper)

        imageAjustes = findViewById(R.id.imageAjustes)


        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }

        val adapter = SemanaAdapter(semanasParaMostrar, peliculaManager) { semanaSeleccionada ->
            irAPeliculas(semanaSeleccionada)
        }
        recyclerView.adapter = adapter
    }

    private fun irAPeliculas(semana: String) {
        val intent = Intent(this, PeliculaActivity::class.java)
        intent.putExtra("SEMANA", semana)
        startActivity(intent)
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