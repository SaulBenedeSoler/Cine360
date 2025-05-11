package com.example.cine360.Activity.Pelicula

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.DataBase.Manager.PeliculasAdminManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class AñadirEditarPeliculaAdminActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir)


        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextGenre = findViewById(R.id.editTextGenre)
        editTextReleaseDate = findViewById(R.id.editTextReleaseDate)
        editTextDuration = findViewById(R.id.editTextDuration)
        editTextImage = findViewById(R.id.editTextImage)
        editTextTrailer = findViewById(R.id.editTextTrailer)
        editTextWeek = findViewById(R.id.editTextWeek)
        buttonSave = findViewById(R.id.buttonSaveMovie)

        peliculasAdminManager = PeliculasAdminManager(this)


        movieId = intent.getIntExtra("MOVIE_ID", -1).takeIf { it != -1 }
        movieId?.let { loadMovieData(it) }

        buttonSave.setOnClickListener {
            saveMovie()
        }
    }

    private fun loadMovieData(movieId: Int) {
        val movie = peliculasAdminManager.getPeliculaById(movieId)
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

    private fun saveMovie() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
        val genre = editTextGenre.text.toString()
        val releaseDate = editTextReleaseDate.text.toString()
        val duration = editTextDuration.text.toString().toIntOrNull() ?: 0
        val image = editTextImage.text.toString()
        val trailer = editTextTrailer.text.toString()
        val week = editTextWeek.text.toString()

        if (title.isNotEmpty() && description.isNotEmpty() && genre.isNotEmpty() && releaseDate.isNotEmpty() && image.isNotEmpty() && trailer.isNotEmpty() && week.isNotEmpty()) {
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

            val result = if (movieId != null) {
                peliculasAdminManager.updatePelicula(pelicula)
            } else {
                peliculasAdminManager.addPelicula(pelicula).toInt()
            }

            if (result > 0) {
                Toast.makeText(this, "Movie saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error saving movie", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        peliculasAdminManager.close()
    }
}