package com.example.cine360.Activity.Comida

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.LoginYRegister.AdminActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.Adapter.ComidaAdminAdapter
import com.example.cine360.DataBase.Manager.ComidaAdminManager
import com.example.cine360.R
import com.example.cine360.DataBase.Tablas.Comida
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ComidaAdminActivity : AppCompatActivity() {

    /*Declaración de variables que más adelante se usaran para llamar tnato a archivos
    * como elementos del xml*/
    private lateinit var recyclerviewComida: RecyclerView
    private lateinit var adapter: ComidaAdminAdapter
    private lateinit var comidaAdminManager: ComidaAdminManager
    private lateinit var fabAddComida: FloatingActionButton
    private lateinit var imageAdminMenu: ImageView

    /*Función para inicializar los diferentes componentes declarados dentro de esta*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_comida)

        /*Asigno a cada variable anteriormente declarada su respectivo
        * objeto xml o archivo*/
        recyclerviewComida = findViewById(R.id.recyclerViewComida)
        fabAddComida = findViewById(R.id.fabAddComida)
        imageAdminMenu = findViewById(R.id.imageAjustes)
        recyclerviewComida.layoutManager = LinearLayoutManager(this)

        comidaAdminManager = ComidaAdminManager(this)

        /*Llamo a la función para cargar la comida*/
        loadComida()

        /*Indico que en el momento en el que el botón sea pulsado este sera
        * dirigido a la vista de añadireditarComida*/
        fabAddComida.setOnClickListener {
            val intent = Intent(this, AñadirEditarComidaAdminActivity::class.java)
            startActivity(intent)
        }
        /*Indico que cuando la imagén sea pulsada mostrar el pop up del menu*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }

    /*Función para cargar la comida de la base de datos*/
    private fun loadComida() {
        /*Declaro una variable que llama a la lista de comidaAdminManager con la cual
        * obtendre los datos.*/
        val comidaList = comidaAdminManager.getAllComida()
        /*Inicializao el adapter y le paso la lista de comida*/
        adapter = ComidaAdminAdapter(this, comidaList,
            /*Indico que cuando le de al botón este me lleve a la clase de editarañadirComida*/
            onEditClick = { comida ->
                val intent = Intent(this, AñadirEditarComidaAdminActivity::class.java)
                intent.putExtra("COMIDA_ID", comida.id)
                startActivity(intent)
            },
            /*Indico que cuando le de al botón este elimine la comida de la base de datos*/
            onDeleteClick = { comida ->
                confirmDeleteComida(comida)
            }
        )
        /*Llamo al adaptador y lo establezco como principal*/
        recyclerviewComida.adapter = adapter
    }

    /*Función que se usara para eliminar menús*/
    private fun confirmDeleteComida(comida: Comida) {
        /*Indico que debe de borrar la comida seleccionada de la base de datos*/
        val rowsDeleted = comidaAdminManager.deleteComida(comida.id)
        /*Compruebo con el if que se a eliminado y si esto a sucedido de forma exitosa
        * entonces muestre un mensaje y cargo todo y si no muestro el mensaje de error*/
        if (rowsDeleted > 0) {
            Toast.makeText(this, "${comida.nombre} deleted", Toast.LENGTH_SHORT).show()
            loadComida()
        } else {
            Toast.makeText(this, "Error deleting ${comida.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    /*Declaro la función para mostrar el menú de administradores*/
    private fun showAdminPopupMenu(anchorView: View) {
        /*Creo una variable y el popMenu anclado en la vista*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        /*Poso los elementos del menu desde pop_admiin_activity*/
        popupMenu.menuInflater.inflate(R.menu.pop_admin_activity, popupMenu.menu)

        /*Infico que cuando se haga click muestre el menu*/
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
                * me lleve a Peliculas*/
                R.id.admin_menu_peliculas -> {
                    startActivity(Intent(this, PeliculaAdminActivity::class.java))
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a Promociones*/
                R.id.admin_menu_promociones -> {
                    startActivity(Intent(this, PromocionesAdminActivity::class.java))
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a comida*/
                R.id.admin_menu_comida -> {

                    Toast.makeText(this, "Ya estás en la administración de comida", Toast.LENGTH_SHORT).show()
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a Usuarios*/
                R.id.admin_menu_usuarios -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a Ajustes*/
                R.id.admin_menu_ajustes -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                else -> false
            }
        }
        /*Indico que muestre el menu*/
        popupMenu.show()
    }

    /*Lla mo a la actividad para comenzar a interactuar con el usuario*/
    override fun onResume() {
        super.onResume()
        loadComida()
    }
    /*Indico que borre todo y el usuario no pueda ver nada*/
    override fun onDestroy() {
        super.onDestroy()
        comidaAdminManager.close()
    }
}