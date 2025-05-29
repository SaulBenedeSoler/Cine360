package com.example.cine360.Activity.LoginYRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.DataBase.Manager.UserManager
import com.example.cine360.R

class RegisterActivity : AppCompatActivity() {

    /*Declaramos las varibales que usaremos*/
    private lateinit var userManager: UserManager
    /*Funcion que se usa al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Declaramos el xml sobre el cual se actuara en este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_register)

        userManager = UserManager(this)
        /*Indicamos los objetos xml que usaremos*/
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.confirmPass)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btniniciarsesion = findViewById<TextView>(R.id.tvIniciarSesion)
        /*Al darle al boton cogera todos los datos de la base de datos, los pasara a string y lo mostrara*/
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            /*Si algo esta vacio se mostrara un mensaje de error*/
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                nombre.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            /*Si lso campos de contraseña no coinciden se mostrara un error*/
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            /*Si todo es correcto se añadira el usuario a la base de datos*/
            val result = userManager.addUser(username, password, nombre, email)
            /*Si todo es correcto se indicara mediante un mensaje*/
            if (result != -1L) {
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar usuario. El nombre de usuario ya existe.", Toast.LENGTH_SHORT).show()
            }
        }
        /*Al pulsar sobre el nos llevara a la vista de inicio de sesion*/
        btniniciarsesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userManager.close()
    }
}