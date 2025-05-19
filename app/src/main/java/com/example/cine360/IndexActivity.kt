package com.example.cine360

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Entrada.EntradaActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Comida

class IndexActivity : AppCompatActivity() {

    private lateinit var imageAjustes: ImageView
    private var usuarioId: Int = -1 //ADDED THIS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        imageAjustes = findViewById(R.id.imageAjustes)
        val dbHelper = DataBaseHelper(this)

        usuarioId = obtenerIdUsuarioActual() //ADDED THIS
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
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
                    val intent = Intent(this, EntradaComidaActivity::class.java)
                    intent.putExtra("USER_ID", usuarioId)
                    startActivity(intent)
                    true
                }
                R.id.menu_entradas_promociones -> {
                    val intent = Intent(this, EntradaPromocionActivity::class.java)
                    intent.putExtra("USER_ID", usuarioId)
                    startActivity(intent)
                    true
                }
                R.id.menu_entradas_peliculas -> {
                    val intent = Intent(this, EntradaActivity::class.java)
                    intent.putExtra("USER_ID", usuarioId)
                    startActivity(intent)
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


    fun irasemana(view: View?) {
        val intent = Intent(this, SemanaActivity::class.java)
        startActivity(intent)
    }

    fun iraProm(view: View?) {
        val intent = Intent(this, PromocionesActivity::class.java)
        startActivity(intent)
    }

    fun iraComida(view: View?) {
        val intent = Intent(this, ComidaActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun obtenerIdUsuarioActual(): Int { //ADDED THIS
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        return usuarioId
    }
}
