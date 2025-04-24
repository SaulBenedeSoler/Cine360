package com.example.cine360.Activity.Semana

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Adapter.SemanaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Manager.SemanaManager
import com.example.cine360.R

class SemanaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semanas)

        val dbHelper = DataBaseHelper(this)
        val db = dbHelper.readableDatabase
        val semanaManager = SemanaManager(dbHelper)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSemanas)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val semanas = semanaManager.obtenerTodasLasSemanas()


        val semanasParaMostrar = if (semanas.isEmpty()) {
            listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")
        } else {
            semanas
        }


        val peliculaManager = PeliculaManager(dbHelper)


        val adapter = SemanaAdapter(semanasParaMostrar, peliculaManager) { semanaSeleccionada ->
            irAPeliculas(semanaSeleccionada)
        }
        recyclerView.adapter = adapter
    }

    private fun irAPeliculas(semana: String) {
        val intent = Intent(this, PeliculaActivity::class.java)
        intent.putExtra("SEMANA", semana)
        startActivity(intent)
    }
}