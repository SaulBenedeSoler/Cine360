package com.example.cine360.Activity.LoginYRegister

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Adapter.UserAdminAdapter
import com.example.cine360.DataBase.Manager.UserManager
import com.example.cine360.DataBase.Tablas.Usuario
import com.example.cine360.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.Activity.Comida.ComidaAdminActivity


class UserAdminActivity : AppCompatActivity() {

    /*Declaramos diferentes variables las cuales seran asignadas diferentes objetos xml o otros archivos*/
    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var adapter: UserAdminAdapter
    private lateinit var userManager: UserManager
    private lateinit var fabAddUser: FloatingActionButton
    private lateinit var imageAdminMenu: ImageView

    /*Funcion que se usa al iniciar la aplicacion*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indicamos que archivo xml es con el que vamos a trabjar para obtener sus objetos y aisgnarles los datos de la base de datos*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_admin)

        /*Asignamos a las variables los archivos xml y otros archivos*/
        recyclerViewUsers = findViewById(R.id.recyclerviewUsers)
        fabAddUser = findViewById(R.id.fabAddUser)
        imageAdminMenu = findViewById(R.id.imageAjustes)
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        userManager = UserManager(this)
        /*Funcion para cargar todos los usuarios*/
        loadUsers()
        /*Si pulsamos el boton vamos a la vista de añadir un usario*/
        fabAddUser.setOnClickListener {
            val intent = Intent(this, AñadirEditarUsuarioAdminActivity::class.java)
            startActivity(intent)
        }
        /*Si pulsamos sobre la imagen nos muestra el menu pop up*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }
    /*Funcion para cargar los usuarios*/
    private fun loadUsers() {
        /*Declaramos una variable que contiene todos los datos de los usuarios*/
        val userList = userManager.getAllUsers()
        /*Llamamos al adaptador y indicamos que coga la lsita de todos los usuarios,
        * además indicamos que cuando le demos a editar este realice la accion correspondiente y lo mismo con
        * el de eliminar*/
        adapter = UserAdminAdapter(this, userList,
            onEditClick = { user ->
                val intent = Intent(this, AñadirEditarUsuarioAdminActivity::class.java).apply {
                    putExtra("USER_ID", user.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { user ->
                confirmDeleteUser(user)
            }
        )
        recyclerViewUsers.adapter = adapter
    }

    /*Funcion en la cual realizamos un aviso antes de eliminar un usuario de la base de datos*/
    private fun confirmDeleteUser(user: Usuario) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar al usuario ${user.username}?")

        builder.setPositiveButton("Sí") { dialog, which ->
            val rowsDeleted = userManager.deleteUser(user.id)
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Usuario ${user.username} eliminado con éxito.", Toast.LENGTH_SHORT).show()
                loadUsers()
            } else {
                Toast.makeText(this, "Error al eliminar el usuario ${user.username}.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(this, "Eliminación cancelada.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /*Funcion para mostrar el menu de administrador*/
    private fun showAdminPopupMenu(anchorView: View) {
        /*Indicamos el archivo xml con el que vamos a trabjar*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_admin_activity, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Nos lleva a la gestion de peliculas*/
                R.id.admin_menu_peliculas -> {
                    startActivity(Intent(this, PeliculaAdminActivity::class.java))
                    true
                }
                /*Nos lleva a la gestion de promociones*/
                R.id.admin_menu_promociones -> {
                    startActivity(Intent(this, PromocionesAdminActivity::class.java))
                    true
                }
                /*Nos lleva a la gestion de comida*/
                R.id.admin_menu_comida -> {
                    startActivity(Intent(this, ComidaAdminActivity::class.java))
                    true
                }
                /*Nos lleva a la gestion de usuarios*/
                R.id.admin_menu_usuarios -> {
                    Toast.makeText(this, "Ya estás en la administración de usuarios", Toast.LENGTH_SHORT).show()
                    true
                }
                /*Nos lleva a la gestion de ajustes*/
                R.id.admin_menu_ajustes -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        userManager.close()
    }
}
