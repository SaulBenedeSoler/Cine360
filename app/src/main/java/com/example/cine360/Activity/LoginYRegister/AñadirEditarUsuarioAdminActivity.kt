package com.example.cine360.Activity.LoginYRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Comida.ComidaAdminActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.DataBase.Manager.UserManager
import com.example.cine360.DataBase.Tablas.Usuario
import com.example.cine360.R

class AñadirEditarUsuarioAdminActivity : AppCompatActivity() {

    /*Declaramos variables que mas adelante usaremos*/
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRole: EditText
    private lateinit var buttonSave: Button
    private lateinit var userManager: UserManager
    private var userId: Int = -1
    private lateinit var imageAdminMenu: ImageView

    /*Funcio para crear y se usa al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indicamos el archivo xml con el que vamos a trabajar*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir_usuario)
        /*Asignamos a las varibales los objetos xml y diferentes archivos*/
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRole = findViewById(R.id.editTextRole)
        buttonSave = findViewById(R.id.buttonSaveUser)
        userManager = UserManager(this)
        imageAdminMenu = findViewById(R.id.imageAjustes)
        /*Comprobamos el usuario */
        userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            title = "Editar Usuario"
            loadUserData(userId)
        } else {
            title = "Añadir Usuario"
        }
        /*Indicamos que utilice la funcio nde saveUser al pulsar sobre el boton*/
        buttonSave.setOnClickListener {
            saveUser()
        }
        /*Indicamos que muestre el menu al pinchar sobre la imagen*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }
    /*Funcion para cargar los datos de los usuarios*/
    private fun loadUserData(userId: Int) {
        /*Llamamos al manager del usuario y obtenemos los datos del usuario filtrado por id*/
        val user = userManager.getUserById(userId)
        /*Asignamos a las variables los datos a mostrar*/
        user?.let {
            editTextUsername.setText(it.username)
            editTextEmail.setText(it.email)
            editTextRole.setText(if (it.isAdmin) "admin" else "usuario")
        } ?: run {
            Toast.makeText(this, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    /*Funcion para guardar cambios en usuarios*/
    private fun saveUser() {
        /*Indicamos que los objetos xml los pase a string al escribir los nuevos datos*/
        val username = editTextUsername.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val role = editTextRole.text.toString().trim()
        /*Comrpobamos que nada este vacio y en caso de estar se le avisa al administrador*/
        if (username.isEmpty() || email.isEmpty() || role.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Por favor, rellena todos los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        /*Compriobamos si es administrador*/
        val isAdmin = role.equals("admin", ignoreCase = true)

        Log.d("AñadirEditarUsuarioAdmin", "Rol ingresado: '$role', isAdmin resultante: $isAdmin")
        /*Creamos una variable y llamamos a la tabla de usuario para pasarle los datos*/
        val newUser = Usuario(userId, username, password, isAdmin, "", email)
        /*Comrpobamos que todo es correcto mediante la id y en caso de serlo se avisa y si a ocurrido un error lo indicamos*/
        if (userId == -1) {
            val newId = userManager.addUser(username, password, "", email, isAdmin)
            if (newId != -1L) {
                Toast.makeText(this, "Usuario añadido correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al añadir el usuario", Toast.LENGTH_SHORT).show()
            }
        } else {
            /*Llamamos al manager para usar la funcion para actualizar los datos de los usuarios*/
            val rowsAffected = userManager.updateUser(newUser)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*Funcion para mostrar el menu pop up*/
    private fun showAdminPopupMenu(anchorView: View) {
        /*Indicamos el archivo xml con el que vamos a trabajar*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_admin_activity, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Si pinchamos vamos a gestionar las peliculas*/
                R.id.admin_menu_peliculas -> {
                    startActivity(Intent(this, PeliculaAdminActivity::class.java))
                    true
                }
                /*Si pinchamos vamos a gestionar las promociones*/
                R.id.admin_menu_promociones -> {
                    startActivity(Intent(this, PromocionesAdminActivity::class.java))
                    true
                }
                /*Si pinchamos vamos a gestionar las comidas*/
                R.id.admin_menu_comida -> {
                    startActivity(Intent(this, ComidaAdminActivity::class.java))
                    true
                }
                /*Si pinchamos vamos a gestionar los usuarios*/
                R.id.admin_menu_usuarios -> {
                    Toast.makeText(
                        this,
                        "Ya estás en la administración de usuarios",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                /*Si pinchamos vamos a ajustes del usuario*/
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