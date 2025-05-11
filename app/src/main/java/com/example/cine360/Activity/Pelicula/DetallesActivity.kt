package com.example.cine360.Activity.Pelicula

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.cine360.Activity.Sala.SalaActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.SalaManager
import com.example.cine360.DataBase.Tablas.Sala
import com.example.cine360.R


class DetallesActivity : AppCompatActivity() {
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var salaManager: SalaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)


        dbHelper = DataBaseHelper(this)


        salaManager = SalaManager(dbHelper)

        val peliculaId = intent.getIntExtra("PELICULA_ID", -1)
        Log.d("DetallesActivity", "ID de película recibido: $peliculaId")

        if (peliculaId == -1) {
            Toast.makeText(this, "Error: No se pudo cargar la película", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        poblarHorariosSalas(peliculaId)
    }

    private fun poblarHorariosSalas(peliculaId: Int) {
        val horariosContainer: LinearLayout = findViewById(R.id.horariosContainer)
        horariosContainer.removeAllViews()

        try {

            val salasAsociadas = mutableListOf<Sala>()
            val horarios = listOf("10:00", "14:30", "19:00")

            for (horario in horarios) {
                val salas = salaManager.obtenerSalasPorPeliculaYHorario(peliculaId, horario)
                salasAsociadas.addAll(salas)
            }

            if (salasAsociadas.isEmpty()) {

                val noRoomsTextView = TextView(this)
                noRoomsTextView.text = "No hay salas disponibles para esta película"
                horariosContainer.addView(noRoomsTextView)
                return
            }


            for (sala in salasAsociadas) {
                val cardView = CardView(this)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(8, 8, 8, 8)
                cardView.layoutParams = layoutParams
                cardView.radius = 8f
                cardView.cardElevation = 4f
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))

                val cardLayout = LinearLayout(this)
                cardLayout.orientation = LinearLayout.VERTICAL
                cardLayout.setPadding(16, 16, 16, 16)


                val textView = TextView(this)
                textView.text = "Sala ${sala.nombre} - Horario: ${sala.horario}"
                cardLayout.addView(textView)

                val button = Button(this)
                button.text = "Ir a la sala"
                button.setOnClickListener {
                    val intent = Intent(this@DetallesActivity, SalaActivity::class.java)
                    intent.putExtra("movieId", peliculaId)
                    intent.putExtra("salaId", sala.id)
                    intent.putExtra("horario", sala.horario)
                    startActivity(intent)
                }
                cardLayout.addView(button)

                cardView.addView(cardLayout)
                horariosContainer.addView(cardView)
            }

        } catch (e: Exception) {
            Log.e("DetallesActivity", "Error al cargar salas", e)
            Toast.makeText(this, "No se pudieron cargar las salas", Toast.LENGTH_SHORT).show()
        }
    }




}