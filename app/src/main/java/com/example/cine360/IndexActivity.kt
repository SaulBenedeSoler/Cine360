package com.example.cine360

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    /*Declaro variables que luego usaremos*/
    private lateinit var imageAjustes: ImageView
    private var usuarioId: Int = -1
    /*Funcion que se usa al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archivo xml que se usara en este caso*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        /*Asigno las variables a objetos xml*/
        imageAjustes = findViewById(R.id.imageAjustes)
        val dbHelper = DataBaseHelper(this)

        /*Obtengo el usuario actual*/
        usuarioId = obtenerIdUsuarioActual()
        /*Al darle al boton mostrara el menu pop up*/
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }
    /*Funcion para ir a la vista de semanas*/
    fun irasemana(view: View?) {
        val intent = Intent(this, SemanaActivity::class.java)
        startActivity(intent)
    }
    /*Funcion para ir a la vista de promociones*/
    fun iraProm(view: View?) {
        val intent = Intent(this, PromocionesActivity::class.java)
        startActivity(intent)
    }
    /*Funcion para ir a la vista de comida*/
    fun iraComida(view: View?) {
        val intent = Intent(this, ComidaActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    /*FUnción para mostrar el menu pop up*/
    private fun showPopupMenu(anchorView: View) {
        /*Indico el archivo xml en el cual v a atrabajar la funcion*/
        val popupMenu = android.widget.PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)

        val userId = obtenerIdUsuarioActual()
        Log.d("ComidaActivity", "User ID from SharedPreferences for menu: $userId")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Indico que me lleve a la vista de promociones*/
                R.id.menu_promociones -> {
                    startActivity(Intent(this, PromocionesActivity::class.java))
                    true
                }
                /*Indico que me lleve a la vista de peliculas*/
                R.id.menu_peliculas -> {
                    val intent = Intent(this, PeliculaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de comida*/
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java))
                    true
                }
                /*Indico que me lleve a la vista de entradas comida*/
                R.id.menu_entradas_comida -> {
                    val intent = Intent(this, EntradaComidaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de entradas  promociones*/
                R.id.menu_entradas_promociones -> {
                    val intent = Intent(this, EntradaPromocionActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de entradas peliculas*/
                R.id.menu_entradas_peliculas -> {
                    val intent = Intent(this, EntradaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de ajustes de usuario*/
                R.id.menu_usuario -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                /*Indico que cierre sesion*/
                R.id.menu_cerrar_sesion -> {
                    LoginActivity.cerrarSesion(this)
                    true
                }
                else -> false
            }
        }
        /*Muestro el pop up */
        popupMenu.show()
    }

    /*Función para que el usuario solo vea sus entradas al pulsar osbre pop up de forma que llame al usuario y compruebe su id*/
    private fun obtenerIdUsuarioActual(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("ComidaActivity", "No se encontró un usuario logueado en SharedPreferences.")
        }
        Log.d("ComidaActivity", "User ID retrieved from SharedPreferences: $usuarioId")
        return usuarioId
    }
}