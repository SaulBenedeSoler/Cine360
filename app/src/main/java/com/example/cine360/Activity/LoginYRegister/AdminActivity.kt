package com.example.cine360.Activity.LoginYRegister

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Comida.ComidaAdminActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.R

class AdminActivity : AppCompatActivity() {
    private lateinit var imageAdminMenu: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indicamos el xml sobre el que se v aa trabajr en este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        /*Obtenemos el nombre del administrador*/
        val username = intent.getStringExtra("USERNAME") ?: "Admin"
        title = "Panel de Administrador - $username"

        /*Declaramos las vriables que contendran los obketos del archivo xml anteriormente indicado*/
        val btnManageUsers = findViewById<Button>(R.id.btnManageUsers)
        val btnManageMovies = findViewById<Button>(R.id.btnManageMovies)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnManagePromociones = findViewById<Button>(R.id.btnManagePromociones)
        val btnManageComida = findViewById<Button>(R.id.btnManageComidas)
        imageAdminMenu = findViewById(R.id.imageAjustes)
        /*Al pulsar sobre el nos lleva a la vista de administrar peliculas*/
        btnManageMovies.setOnClickListener {
            val intent = Intent(this, PeliculaAdminActivity::class.java)
            startActivity(intent)
        }
        /*Al pulsar sobre el nos lleva a la vista de administrar promociones*/
        btnManagePromociones.setOnClickListener {
            val intent = Intent(this, PromocionesAdminActivity::class.java)
            Log.d("AdminActivity", "Starting activity: ${intent.component?.className}")
            startActivity(intent)
        }
        /*Al pulsar sobre el nos lleva a la vista de administrar comida*/
        btnManageComida.setOnClickListener {
            val intent = Intent(this, ComidaAdminActivity::class.java)
            startActivity(intent)
        }
        /*Al pulsar sobre el cierra sesion*/
        btnLogout.setOnClickListener {
            cerrarSesion(this)
        }
        /*Al pulsar sobre el nos lleva a la vista de administrar usuarios*/
        btnManageUsers.setOnClickListener{
            val intent = Intent(this,UserAdminActivity::class.java)
            startActivity(intent)
        }

        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }

    /*Funcion para cerrar sesion en la cual comprobamos el id de cada uno y se cierra sesion de ese usuario*/
    companion object {
        fun cerrarSesion(context: Context) {
            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }


    private fun showAdminPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_admin_activity, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.admin_menu_peliculas -> {
                    startActivity(Intent(this, PeliculaAdminActivity::class.java))
                    true
                }
                R.id.admin_menu_promociones -> {
                    startActivity(Intent(this, PromocionesAdminActivity::class.java))
                    true
                }
                R.id.admin_menu_comida -> {
                    startActivity(Intent(this, ComidaAdminActivity::class.java))
                    true
                }
                R.id.admin_menu_usuarios -> {

                    Toast.makeText(this, "Ya estás en la administración de usuarios", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.admin_menu_ajustes -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}