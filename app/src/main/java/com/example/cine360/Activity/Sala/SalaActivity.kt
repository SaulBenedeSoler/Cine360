package com.example.cine360.Activity.Sala

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Entrada.EntradaActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradaManager
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Manager.SalaManager
import com.example.cine360.R

class SalaActivity : AppCompatActivity() {

    private lateinit var recyclerViewAsientos: RecyclerView
    private lateinit var btnConfirmar: Button
    private var salaId: Int = 0
    private var asientos: MutableList<String> = mutableListOf()
    private var asientosSeleccionados: MutableList<Int> = mutableListOf()
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var movieManager: PeliculaManager
    private lateinit var entradaManager: EntradaManager
    private var movieIdSeleccionado: Int = -1
    private lateinit var imageAjustes: ImageView
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sala)

        context = this
        dbHelper = DataBaseHelper(context)
        movieManager = PeliculaManager(dbHelper)
        entradaManager = EntradaManager(dbHelper)
        recyclerViewAsientos = findViewById(R.id.recyclerViewAsientos)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        imageAjustes = findViewById(R.id.imageAjustes)

        btnConfirmar.setOnClickListener { confirmarSeleccion() }
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }

        val movieId = intent.getIntExtra("movieId", -1)
        movieIdSeleccionado = movieId
        val horario = intent.getStringExtra("horario")


        if (movieId != -1 && horario != null) {
            generarSala(movieId, horario)
        } else {
            Toast.makeText(context, "ID de película o horario no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun generarSala(movieId: Int, horario: String) {
        val salaManager = SalaManager(dbHelper)
        val salas = salaManager.obtenerSalasPorPeliculaYHorario(movieId, horario)

        if (salas.isNotEmpty()) {
            val sala = salas[0]
            salaId = sala.id.toInt()

            val pelicula = movieManager.obtenerPeliculaPorId(movieId.toLong().toInt())

            if (pelicula != null) {
                findViewById<TextView>(R.id.textViewSalaTitulo).text =
                    "${pelicula.titulo} - Sala: ${sala.nombre} - Horario: ${sala.horario}"
                val asientosString =
                    salaManager.obtenerAsientos(salaId.toLong()) ?: generarAsientosIniciales(salaId)
                asientos = asientosString.split(",").toMutableList()
                mostrarAsientos(asientos, 5)
            } else {
                Toast.makeText(context, "Película no encontrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(context, "Sala no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun generarAsientosIniciales(salaId: Int): String {
        val asientos = "O".repeat(80).chunked(1).joinToString(",")
        SalaManager(dbHelper).actualizarAsientos(
            dbHelper.writableDatabase,
            salaId.toLong(),
            asientos
        )
        return asientos
    }

    private fun actualizarAsientoEnBaseDeDatos(
        salaId: Int,
        asientoIndex: Int,
        nuevoEstado: String
    ) {
        val salaManager = SalaManager(dbHelper)
        val salaIdLong: Long = salaId.toLong()
        val asientosString = salaManager.obtenerAsientos(salaIdLong) ?: return

        val asientos = asientosString.split(",").toMutableList()
        asientos[asientoIndex] = nuevoEstado

        salaManager.actualizarAsientos(
            dbHelper.writableDatabase,
            salaIdLong,
            asientos.joinToString(",")
        )
        Log.d("SalaActivity", "Asiento $asientoIndex actualizado a $nuevoEstado")
    }

    private fun mostrarAsientos(asientos: MutableList<String>, columnas: Int) {
        recyclerViewAsientos.layoutManager =
            GridLayoutManager(context, columnas)

        recyclerViewAsientos.adapter =
            object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_asiento, parent, false)
                    return object : RecyclerView.ViewHolder(view) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    val asientoView =
                        holder.itemView.findViewById<TextView>(R.id.textViewAsiento)
                    asientoView.text = (position + 1).toString()

                    when (asientos[position]) {
                        "O" -> asientoView.setBackgroundResource(R.drawable.asiento_libre)
                        "S" -> asientoView.setBackgroundResource(R.drawable.asiento_ocupado)
                        "X" -> asientoView.setBackgroundResource(R.drawable.asiento_ocupado)
                    }

                    holder.itemView.setOnClickListener {
                        if (position < asientos.size) {
                            when (asientos[position]) {
                                "O" -> {
                                    asientos[position] = "S"
                                    asientosSeleccionados.add(position)
                                    Toast.makeText(
                                        this@SalaActivity,
                                        "Asiento ${position + 1} seleccionado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                "S" -> {
                                    asientos[position] = "O"
                                    asientosSeleccionados.remove(position)
                                    Toast.makeText(
                                        this@SalaActivity,
                                        "Asiento ${position + 1} liberado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                "X" -> {
                                    Toast.makeText(
                                        this@SalaActivity,
                                        "Este asiento ya está ocupado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@setOnClickListener
                                }
                            }
                            actualizarAsientoEnBaseDeDatos(
                                salaId,
                                position,
                                asientos[position]
                            )
                            notifyItemChanged(position)
                        }
                    }
                }

                override fun getItemCount(): Int = asientos.size
            }
    }

    private fun confirmarSeleccion() {
        if (asientosSeleccionados.isNotEmpty()) {
            for (position in asientosSeleccionados) {
                actualizarAsientoEnBaseDeDatos(salaId, position, "X")
            }
            Toast.makeText(
                this,
                "${asientosSeleccionados.size} asientos confirmados",
                Toast.LENGTH_SHORT
            ).show() // Use context

            val sala = SalaManager(dbHelper).obtenerSalaPorId(
                dbHelper.readableDatabase,
                salaId.toLong()
            )
            val pelicula = movieManager.obtenerPeliculaPorId(movieIdSeleccionado.toInt())

            val userId = obtenerIdUsuarioActual(this)

            for (position in asientosSeleccionados) {
                val fila = position / 5 + 1;
                val asiento = position % 5 + 1;
                entradaManager.crearEntrada(
                    userId,
                    pelicula?.id?.toInt() ?: -1,
                    sala?.nombre ?: "",
                    asiento,  // Pass the calculated asiento
                    fila, //pass the calculated fila
                    sala?.horario ?: "",
                    pelicula?.titulo ?: "Desconocido"
                )
            }

            startActivity(Intent(this, EntradaActivity::class.java).putExtra("USER_ID", userId))
        } else {
            Toast.makeText(this, "No hay asientos seleccionados", Toast.LENGTH_SHORT).show()
        }
        asientosSeleccionados.clear()
    }

    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("SalaActivity", "No se encontró un usuario logueado")
        }
        return usuarioId
    }

    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView, Gravity.END) // Use context
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_promociones -> {
                    startActivity(Intent(this, PromocionesActivity::class.java)) // Use context
                    true
                }
                R.id.menu_peliculas -> {
                    startActivity(Intent(this, SemanaActivity::class.java)) // Use context
                    true
                }
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java)) // Use context
                    true
                }

                R.id.menu_entradas_comida -> {
                    startActivity(Intent(this, EntradaComidaActivity::class.java)) // Use context
                    true
                }
                R.id.menu_entradas_promociones -> {
                    startActivity(Intent(this, EntradaPromocionActivity::class.java)) // Use context
                    true
                }
                R.id.menu_entradas_peliculas -> {
                    startActivity(Intent(this, EntradaActivity::class.java)) // Use context
                    true
                }

                R.id.menu_usuario -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java)) // Use context
                    true
                }

                R.id.menu_cerrar_sesion -> {
                    LoginActivity.cerrarSesion(this) // Use context
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }
}
