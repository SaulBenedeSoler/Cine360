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

    /*Declaro las variables necesarias apra objetos xml o otros archivos*/
    private lateinit var recyclerViewAsientos: RecyclerView
    private lateinit var btnConfirmar: Button
    private var salaId: Int = 0
    private var asientosEstado: MutableList<String> = mutableListOf()
    private var asientosSeleccionados: MutableList<Int> = mutableListOf()
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var movieManager: PeliculaManager
    private lateinit var entradaManager: EntradaManager
    private var movieIdSeleccionado: Int = -1
    private lateinit var imageAjustes: ImageView
    private lateinit var context: Context
    private val numColumnas = 5
    /*Funcion que se usa al inicir la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archivo xml al que se asocia este documento*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sala)
        /*Croe una instancia con la base de datos y asigno los objetso xml a las variables*/
        context = this
        dbHelper = DataBaseHelper(context)
        movieManager = PeliculaManager(dbHelper)
        entradaManager = EntradaManager(dbHelper)
        recyclerViewAsientos = findViewById(R.id.recyclerViewAsientos)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        imageAjustes = findViewById(R.id.imageAjustes)
        /*All darle al boton uso la funcion de confirmar seleccion*/
        btnConfirmar.setOnClickListener { confirmarSeleccion() }
        /*Al darle al boton muestro el menu pop up*/
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
        /*Obtengo la pelicula por id y el horario de este*/
        val movieId = intent.getIntExtra("movieId", -1)
        movieIdSeleccionado = movieId
        val horario = intent.getStringExtra("horario")
        /*Indico que si la pelicula esta alamcenada en la base de datos entonces genera las salas asignadas a esta*/
        if (movieId != -1 && horario != null) {
            generarSala(movieId, horario)
        } else {
            Toast.makeText(context, "ID de película o horario no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*Fincion para crear la sala*/
    private fun generarSala(movieId: Int, horario: String) {
        /*Creo una instancia entre el manager y la base dedatos*/
        val salaManager = SalaManager(dbHelper)
        val salas = salaManager.obtenerSalasPorPeliculaYHorario(movieId, horario)
        /*Indico que si la sala no esta vacia muestre su id y su pelicula*/
        if (salas.isNotEmpty()) {
            val sala = salas[0]
            salaId = sala.id.toInt()

            val pelicula = movieManager.obtenerPeliculaPorId(movieId.toLong().toInt())
            /*Si la pelicula no existe muestro un mensaje de error*/
            if (pelicula != null) {
                findViewById<TextView>(R.id.textViewSalaTitulo).text =
                    "${pelicula.titulo} - Sala: ${sala.nombre} - Horario: ${sala.horario}"
                val asientosString =
                    salaManager.obtenerAsientos(salaId.toLong()) ?: generarAsientosIniciales(salaId)
                asientosEstado = asientosString.split(",").toMutableList()
                mostrarAsientos(asientosEstado, numColumnas)
            } else {
                Toast.makeText(context, "Película no encontrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(context, "Sala no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*Funcion que usamos para crear los asientos de la sala*/
    private fun generarAsientosIniciales(salaId: Int): String {
        val asientos = "O".repeat(40).chunked(1).joinToString(",")
        SalaManager(dbHelper).actualizarAsientos(
            dbHelper.writableDatabase,
            salaId.toLong(),
            asientos
        )
        return asientos
    }

    /*Funcion para acutalizar el estado de los asientos en la base de datos*/
    private fun actualizarAsientoEnBaseDeDatos(
        salaId: Int,
        asientoIndex: Int,
        nuevoEstado: String
    ) {
        /*Llamo al manager y uso una instancia de la base de datos, asi ibtengo todos los asientos*/
        val salaManager = SalaManager(dbHelper)
        val salaIdLong: Long = salaId.toLong()
        val asientosString = salaManager.obtenerAsientos(salaIdLong) ?: return
        /*Divido los asientos en una lista sparada*/
        val asientos = asientosString.split(",").toMutableList()
        asientos[asientoIndex] = nuevoEstado
        /*Actualizo los asientos*/
        salaManager.actualizarAsientos(
            dbHelper.writableDatabase,
            salaIdLong,
            asientos.joinToString(",")
        )
        Log.d("SalaActivity", "Asiento $asientoIndex actualizado a $nuevoEstado")
    }

    /*Funcion para mostrar asientos*/
    private fun mostrarAsientos(asientos: MutableList<String>, columnas: Int) {
        /*Asigno a las variables diferentes objetos del archivo xml*/
        recyclerViewAsientos.layoutManager = GridLayoutManager(context, columnas)
        recyclerViewAsientos.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_asiento, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }
            /*Indico que debe de coger cada apartado y mostrar si esta ocupado o libre el asiento*/
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val imageViewAsiento = holder.itemView.findViewById<ImageView>(R.id.textViewAsiento)
                val resources = context.resources

                when (asientos[position]) {
                    "O" -> {
                        /*Indico que el asiento esta libre*/
                        val resourceId = resources.getIdentifier("asientolibre", "drawable", context.packageName)
                        imageViewAsiento.setImageResource(resourceId)
                    }
                    "S" -> {
                        /*Indico que el asiento esta seleccionado*/
                        val resourceId = resources.getIdentifier("asientoseleccionado", "drawable", context.packageName)
                        imageViewAsiento.setImageResource(resourceId)
                    }
                    "X" -> {
                        /*Indico que el asiento esta ocupado*/
                        val resourceId = resources.getIdentifier("asientoocupado", "drawable", context.packageName)
                        imageViewAsiento.setImageResource(resourceId)
                    }
                }
                /*Indico que al marcar cambie el estado de cada asiento*/
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
    /*Funcion para confirmar el asiento seleccionado*/
    private fun confirmarSeleccion() {
        /*Si el asiento no esta vacio creo una llamada al manager y una instancia con la base de datos,
        * llamo al manager de peliculas para obtener las peliculas por id y asi filtrar las sals
        * y llamo al id del usuario para comprobar quien es*/
        if (asientosSeleccionados.isNotEmpty()) {
            val salaManager = SalaManager(dbHelper)
            val sala = salaManager.obtenerSalaPorId(dbHelper.readableDatabase, salaId.toLong())
            val pelicula = movieManager.obtenerPeliculaPorId(movieIdSeleccionado)
            val userId = obtenerIdUsuarioActual(this)
            /*Claculo la posicion de los asientos seleccionados y creo una entrada llamando al manage rde entradas usando los datos declarados
            * en la tabla de la base de datos de entradas*/
            for (position in asientosSeleccionados) {
                actualizarAsientoEnBaseDeDatos(salaId, position, "X")
                val fila = position / numColumnas + 1
                val asiento = position % numColumnas + 1
                entradaManager.crearEntrada(
                    userId,
                    pelicula?.id?.toInt() ?: -1,
                    sala?.nombre ?: "",
                    asiento,
                    fila,
                    sala?.horario ?: "",
                    pelicula?.titulo ?: "Desconocido"
                )
            }
            /*Si todo sale bien lo indicamos y en caso de error mostramos el mensaje de error*/
            Toast.makeText(
                this,
                "${asientosSeleccionados.size} asientos confirmados",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this, EntradaActivity::class.java).putExtra("USER_ID", userId))
            finish()
        } else {
            Toast.makeText(this, "No hay asientos seleccionados", Toast.LENGTH_SHORT).show()
        }
        /*Limpiamos los asientos seleccionados*/
        asientosSeleccionados.clear()
    }


    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("usuario_id", -1)
    }

    /*Funcion para mostrar el menu pop up*/
    private fun showPopupMenu(anchorView: View) {
        /*Indico el xml sobre el cual se va a trabajar*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)
        /*Obtengo el id del usuario*/
        val userId = obtenerIdUsuarioActual()
        Log.d("PromocionesActivity", "User ID from SharedPreferences for menu: $userId")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Me lleva  al la vista promociones*/
                R.id.menu_promociones -> {
                    startActivity(Intent(this, PromocionesActivity::class.java))
                    true
                }
                /*Me lleva  al la vista Peliculas*/
                R.id.menu_peliculas -> {
                    val intent = Intent(this, SemanaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Me lleva  al la vista Comida*/
                R.id.menu_comida -> {
                    val intent = Intent(this, ComidaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Me lleva  al la vista Entrada comida*/
                R.id.menu_entradas_comida -> {
                    val intent = Intent(this, EntradaComidaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Me lleva  al la vista Entrada Promociones*/
                R.id.menu_entradas_promociones -> {
                    val intent = Intent(this, EntradaPromocionActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Me lleva  al la vista Entrada Peliculas*/
                R.id.menu_entradas_peliculas -> {
                    val intent = Intent(this, EntradaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Me lleva  al la vista Ajustes de usuario*/
                R.id.menu_usuario -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                /*Cierra sesion*/
                R.id.menu_cerrar_sesion -> {
                    LoginActivity.cerrarSesion(this)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    /*Funcion para obtener el usuario actual*/
    private fun obtenerIdUsuarioActual(): Int {
        /*Compruebo el id del usuario que existe y si no existe indico un mensaje de error*/
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("PromocionesActivity", "No se encontró un usuario logueado en SharedPreferences.")
        }
        Log.d("PromocionesActivity", "User ID retrieved from SharedPreferences: $usuarioId")
        return usuarioId
    }
}