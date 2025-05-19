package com.example.cine360.Activity.LoginYRegister

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Comida.ComidaAdminActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.R

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val username = intent.getStringExtra("USERNAME") ?: "Admin"
        title = "Panel de Administrador - $username"

        val btnManageUsers = findViewById<Button>(R.id.btnManageUsers)
        val btnManageMovies = findViewById<Button>(R.id.btnManageMovies)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnManagePromociones = findViewById<Button>(R.id.btnManagePromociones)
        val btnManageComida = findViewById<Button>(R.id.btnManageComidas)

        btnManageMovies.setOnClickListener {
            val intent = Intent(this, PeliculaAdminActivity::class.java)
            startActivity(intent)
        }

        btnManagePromociones.setOnClickListener {
            val intent = Intent(this, PromocionesAdminActivity::class.java)
            Log.d("AdminActivity", "Starting activity: ${intent.component?.className}")
            startActivity(intent)
        }

        btnManageComida.setOnClickListener {
            val intent = Intent(this, ComidaAdminActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            cerrarSesion(this)
        }
    }

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
}