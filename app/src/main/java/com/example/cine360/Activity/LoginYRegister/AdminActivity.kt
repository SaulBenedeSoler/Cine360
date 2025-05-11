package com.example.cine360.Activity.LoginYRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
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

        btnManageMovies.setOnClickListener {
            val intent = Intent(this, PeliculaAdminActivity::class.java)
            startActivity(intent)
        }



    }

}
