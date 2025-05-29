package com.example.cine360.Activity.Pelicula

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Comida.ComidaAdminActivity
import com.example.cine360.Activity.LoginYRegister.AdminActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.DataBase.Manager.PeliculasAdminManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class AñadirEditarPeliculaAdminActivity : AppCompatActivity() {

    /*Delcaro diferentes variables que llaman a objetos xml o archivos*/
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextGenre: EditText
    private lateinit var editTextReleaseDate: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var editTextImage: EditText
    private lateinit var editTextTrailer: EditText
    private lateinit var editTextWeek: EditText
    private lateinit var buttonSave: Button
    private lateinit var peliculasAdminManager: PeliculasAdminManager
    private var movieId: Int? = null
    private lateinit var imageAdminMenu: ImageView

    /*Función usada al ejecutar la app por primera vez y inicializar todo*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico cual sera el xml sobre el que trabajara el archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir)

        /*Llamo a las variables anteriormente declaradas y les asigno sus objetos xml
        * o arhcivos*/
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextGenre = findViewById(R.id.editTextGenre)
        editTextReleaseDate = findViewById(R.id.editTextReleaseDate)
        editTextDuration = findViewById(R.id.editTextDuration)
        editTextImage = findViewById(R.id.editTextImage)
        editTextTrailer = findViewById(R.id.editTextTrailer)
        editTextWeek = findViewById(R.id.editTextWeek)
        buttonSave = findViewById(R.id.buttonSaveMovie)
        imageAdminMenu = findViewById(R.id.imageAjustes)

        peliculasAdminManager = PeliculasAdminManager(this)

        /*Indico que coga el id y en caso de no exisitir que cree uno nuevo*/
        movieId = intent.getIntExtra("MOVIE_ID", -1).takeIf { it != -1 }
        /*Llamo a la función para cargar las peliculas*/
        movieId?.let { loadMovieData(it) }
        /*Indico que cuando pulse el botón llame a la función guardar pelicula*/
        buttonSave.setOnClickListener {
            saveMovie()
        }
        /*Indico que cuando pulse la imagen este muestre el pop up*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }

    /*Funcion para cargar todos los datos de peliculas*/
    private fun loadMovieData(movieId: Int) {
        /*Delcaro una variable y le indico que coga todas las pelicula por id*/
        val movie = peliculasAdminManager.getPeliculaById(movieId)
        /*En caso de ser corecto muestro estos datos asignandolos a las variables anteriormente creadas, en caso de fallo muestro
        * mensaje de error*/
        movie?.let {
            editTextTitle.setText(it.titulo)
            editTextDescription.setText(it.descripcion)
            editTextGenre.setText(it.genero)
            editTextReleaseDate.setText(it.fechaLanzamiento)
            editTextDuration.setText(it.duracion.toString())
            editTextImage.setText(it.imagen)
            editTextTrailer.setText(it.trailer)
            editTextWeek.setText(it.semana)
        } ?: run {
            Toast.makeText(this, "Error loading movie data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*Función para guardar peliculas*/
    private fun saveMovie() {
        /*Declaro variables y llamos los datos y lso paso todos a string*/
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val genre = editTextGenre.text.toString().trim()
        val releaseDate = editTextReleaseDate.text.toString().trim()
        val durationStr = editTextDuration.text.toString().trim()
        val image = editTextImage.text.toString().trim()
        val trailer = editTextTrailer.text.toString().trim()
        val week = editTextWeek.text.toString().trim()

        /*Compruebo que todos los datos esten rellenos*/
        if (title.isNotEmpty() && description.isNotEmpty() && genre.isNotEmpty() && releaseDate.isNotEmpty() && durationStr.isNotEmpty() && image.isNotEmpty() && trailer.isNotEmpty() && week.isNotEmpty()) {
            val duration = durationStr.toIntOrNull()
            if (duration == null) {
                Toast.makeText(this, "Por favor, introduce una duración válida", Toast.LENGTH_SHORT).show()
                return
            }
            /*Declaro una variable y llamo a la tabla de creación de películas*/
            val pelicula = Pelicula(
                movieId ?: 0,
                title,
                description,
                genre,
                releaseDate,
                duration,
                image,
                trailer,
                week
            )
            /*En caso de ser correcto indico que debe de actualizar o añadir una nueva pelicula a la base de datos*/
            val result = if (movieId != null) {
                peliculasAdminManager.updatePelicula(pelicula)
            } else {
                peliculasAdminManager.addPelicula(pelicula).toInt()
            }
            /*Si el resultado es corecto entonces muestro un mensaje, si no es correcto otro y en caso
            * de no rellenar un campo muestro también un mensaje*/
            if (result > 0) {
                Toast.makeText(this, "Pelicula guardada con exito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar la pelicula", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Porfavor rellena los campos", Toast.LENGTH_SHORT).show()
        }
    }

    /*FUnción para mostrar el pop up del administrador*/
    private fun showAdminPopupMenu(anchorView: View) {
        /*Delcaro en que archivo xml va a trabajar esta funcion*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_admin_activity, popupMenu.menu)

        /*Indico que cuando este sea pulsado debe mostrar el contenido*/
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Indico que me lleve a Pelicula cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_peliculas -> {
                    Toast.makeText(this, "Ya estás en la administración de películas", Toast.LENGTH_SHORT).show()
                    true
                }
                /*Indico que me lleve a Promociones cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_promociones -> {
                    startActivity(Intent(this, PromocionesAdminActivity::class.java))
                    true
                }
                /*Indico que me lleve a Comida cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_comida -> {
                    startActivity(Intent(this, ComidaAdminActivity::class.java))
                    true
                }
                /*Indico que me lleve a Usuarios cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_usuarios -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                /*Indico que me lleve a Ajustes cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_ajustes -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                else -> false
            }
        }
        /*Muestro el pop up*/
        popupMenu.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        peliculasAdminManager.close()
    }
}
