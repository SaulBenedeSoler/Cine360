package com.example.cine360.Activity.Pelicula

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Adapter.PeliculaAdminAdapter
import com.example.cine360.DataBase.Manager.PeliculasAdminManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PeliculaAdminActivity : AppCompatActivity() {
    private lateinit var recyclerViewMovies: RecyclerView
    private lateinit var adapter: PeliculaAdminAdapter
    private lateinit var peliculasAdminManager: PeliculasAdminManager
    private lateinit var fabAddMovie: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pelicula)

        recyclerViewMovies = findViewById(R.id.recyclerViewMovies)
        fabAddMovie = findViewById(R.id.fabAddMovie)
        recyclerViewMovies.layoutManager = LinearLayoutManager(this)

        peliculasAdminManager = PeliculasAdminManager(this)

        loadMovies()

        fabAddMovie.setOnClickListener {
            val intent = Intent(this, AñadirEditarPeliculaAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadMovies() {
        val movieList = peliculasAdminManager.getAllPeliculas()
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

    private fun confirmDeleteMovie(pelicula: Pelicula) {
        val rowsDeleted = peliculasAdminManager.deletePelicula(pelicula.id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "${pelicula.titulo} deleted", Toast.LENGTH_SHORT).show()
            loadMovies()
        } else {
            Toast.makeText(this, "Error deleting ${pelicula.titulo}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        peliculasAdminManager.close()
    }
}
