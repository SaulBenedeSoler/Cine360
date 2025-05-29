package com.example.cine360.Activity.Pelicula

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
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.Adapter.PeliculaAdminAdapter
import com.example.cine360.DataBase.Manager.PeliculasAdminManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PeliculaAdminActivity : AppCompatActivity() {

    /*Declaro variables que usaremos para trabjaar con xml, objetos del xml y archivos*/
    private lateinit var recyclerViewMovies: RecyclerView
    private lateinit var adapter: PeliculaAdminAdapter
    private lateinit var peliculasAdminManager: PeliculasAdminManager
    private lateinit var fabAddMovie: FloatingActionButton
    private lateinit var imageAdminMenu: ImageView

    /*Funcion que se usa al crear la aplicación*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archiuvo xml sobre el que se va a trabajar en este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pelicula)

        /*Llamo a las variables creadas anteriormnete y les asigno valores o archivos u objetos xml*/
        recyclerViewMovies = findViewById(R.id.recyclerViewMovies)
        fabAddMovie = findViewById(R.id.fabAddMovie)
        imageAdminMenu = findViewById(R.id.imageAjustes)
        recyclerViewMovies.layoutManager = LinearLayoutManager(this)

        peliculasAdminManager = PeliculasAdminManager(this)
        /*Funcion para cargar las peliculas*/
        loadMovies()
        /*Boton que al darle inicia la actividad de añadir peliculas*/
        fabAddMovie.setOnClickListener {
            val intent = Intent(this, AñadirEditarPeliculaAdminActivity::class.java)
            startActivity(intent)
        }
        /*Al pulsar el boton me muestra el menu pop up*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }
    /*Funcion para cargar las peliculas*/
    private fun loadMovies() {
        /*Creo una variable y le mando todas las peliculas almacenadas en la base de datos*/
        val movieList = peliculasAdminManager.getAllPeliculas()
        /*Uso el adapter para obtener las peliculas y si le doy al boton de editar inicar
        * esa accion llamando al archivo correspondiente y se hace lo mismo al darle a eliminar*/
        adapter = PeliculaAdminAdapter(this, movieList,
            onEditClick = { pelicula ->
                val intent = Intent(this, AñadirEditarPeliculaAdminActivity::class.java)
                intent.putExtra("MOVIE_ID", pelicula.id)
                startActivity(intent)
            },
            onDeleteClick = { pelicula ->
                confirmDeleteMovie(pelicula)
            }
        )
        recyclerViewMovies.adapter = adapter
    }

    /*Funcion para eliminar pelicula*/
    private fun confirmDeleteMovie(pelicula: Pelicula) {
        val rowsDeleted = peliculasAdminManager.deletePelicula(pelicula.id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "${pelicula.titulo} deleted", Toast.LENGTH_SHORT).show()
            loadMovies()
        } else {
            Toast.makeText(this, "Error deleting ${pelicula.titulo}", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        loadMovies()
    }

    override fun onDestroy() {
        super.onDestroy()
        peliculasAdminManager.close()
    }
}