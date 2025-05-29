package com.example.cine360.Activity.Pelicula

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.cine360.Activity.Sala.SalaActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.SalaManager
import com.example.cine360.DataBase.Tablas.Sala
import com.example.cine360.R
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Entrada.EntradaActivity


class DetallesActivity : AppCompatActivity() {
    /*Declar las variables que usaremos*/
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var salaManager: SalaManager
    private lateinit var imageAjustes: ImageView

    /*Funcion que se usa el iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el xml sobre el cual se va a trabajar*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        /*Inicializo una llamada con la base de datos
        * y llamo a otros archivos y los asigno a sus variables*/
        dbHelper = DataBaseHelper(this)
        salaManager = SalaManager(dbHelper)


        imageAjustes = findViewById(R.id.imageAjustes)

        val peliculaId = intent.getIntExtra("PELICULA_ID", -1)
        Log.d("DetallesActivity", "ID de película recibido: $peliculaId")

        if (peliculaId == -1) {
            Toast.makeText(this, "Error: No se pudo cargar la película", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        /*Llamo a la funcion de poblarhorarios y salas para ejecutarla*/
        poblarHorariosSalas(peliculaId)

        /*Cuando le de me muestra el pop up*/
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    /*Funcion para asignar las salas y horarios*/
    private fun poblarHorariosSalas(peliculaId: Int) {
        /*Indicamos el objeto xml que se usara para mostrarlo*/
        val horariosContainer: LinearLayout = findViewById(R.id.horariosContainer)
        horariosContainer.removeAllViews()

        try {
            /*Cogemos la lista de salas y asignamos una lista de horarios*/
            val salasAsociadas = mutableListOf<Sala>()
            val horarios = listOf("10:00", "14:30", "19:00")

            /*Llamos a la variables horarios y indicamos que use al managere para obtener las salas por horario*/
            for (horario in horarios) {
                val salas = salaManager.obtenerSalasPorPeliculaYHorario(peliculaId, horario)
                salasAsociadas.addAll(salas)
            }
            /*Si no existen se muestra un mensaje de error*/
            if (salasAsociadas.isEmpty()) {
                val noRoomsTextView = TextView(this)
                noRoomsTextView.text = "No hay salas disponibles para esta película"
                horariosContainer.addView(noRoomsTextView)
                return
            }
            /*Obtenemos el usuario que se encuentra realizando la accion*/
            val userId = obtenerIdUsuarioActual()
            Log.d("DetallesActivity", "Current User ID for SalaActivity: $userId")

            /*Iteramos sobre las diferentes salas*/
            for (sala in salasAsociadas) {
               /*Llamamos al objeto xml con el que vamos a trabajar*/
                val cardView = layoutInflater.inflate(R.layout.activity_card_sala_item, horariosContainer, false) as CardView
                /*Asignamos a variables los objetos del archivo xml*/
                val titleTextView = cardView.findViewById<TextView>(R.id.textViewNombreSala)
                val scheduleTextView = cardView.findViewById<TextView>(R.id.textViewHorarioSala)
                val button = cardView.findViewById<Button>(R.id.buttonVerDetalles)
                /*Le pasamos a estas variables los datos de la sala*/
                titleTextView.text = sala.nombre
                scheduleTextView.text = "Horario: ${sala.horario}"
                /*Indicamos que cuando pulse el boton inserte los datos y inicie la actividad para crear la sala*/
                button.setOnClickListener {
                    val intent = Intent(this@DetallesActivity, SalaActivity::class.java)
                    intent.putExtra("movieId", peliculaId)
                    intent.putExtra("salaId", sala.id)
                    intent.putExtra("horario", sala.horario)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                }
                /*Añadimos los horarios*/
                horariosContainer.addView(cardView)
            }

        } catch (e: Exception) {
            Log.e("DetallesActivity", "Error al cargar salas", e)
            Toast.makeText(this, "No se pudieron cargar las salas", Toast.LENGTH_SHORT).show()
        }
    }

    /*Funcion para mostrar el pop up*/
    private fun showPopupMenu(anchorView: View) {
        /*Se declara cual va a ser el archivo xml sobre el cual vamos a trabajr*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)
        /*Llamamos a la función obtenerUsuarioActual para comprobar que usuario esta accediendo
        * y que de esta forma solo se le muestren los daots correspondientes a este*/
        val userId = obtenerIdUsuarioActual()
        Log.d("DetallesActivity", "User ID from SharedPreferences for menu: $userId")

        /*Indicamos que al pulsar se muestren las diferentes opciones*/
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Te redirige a la vista de Promociones*/
                R.id.menu_promociones -> {
                    val intent = Intent(this, PromocionesActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Te redirige a la vista de Peliculas*/
                R.id.menu_peliculas -> {
                    val intent = Intent(this, PeliculaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Te redirige a la vista de Comidas*/
                R.id.menu_comida -> {
                    val intent = Intent(this, ComidaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Te redirige a la vista de Entradas Comida*/
                R.id.menu_entradas_comida -> {
                    val intent = Intent(this, EntradaComidaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Te redirige a la vista de Entradas Promociones*/
                R.id.menu_entradas_promociones -> {
                    val intent = Intent(this, EntradaPromocionActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Te redirige a la vista de Entradas Peliculas*/
                R.id.menu_entradas_peliculas -> {
                    val intent = Intent(this, EntradaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Te redirige a la vista de Ajustes de usuario*/
                R.id.menu_usuario -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                /*Cierra la sesion del usuario*/
                R.id.menu_cerrar_sesion -> {
                    LoginActivity.cerrarSesion(this)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    /*Funcion con la cual obtenemos el id del usuario para saber quien es y en caso de que no exista se meustra un error o si no esta registrado
    * o iniciado sesion no se le permite interactuar*/
    private fun obtenerIdUsuarioActual(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("DetallesActivity", "No se encontró un usuario logueado en SharedPreferences.")
        }
        Log.d("DetallesActivity", "User ID retrieved from SharedPreferences: $usuarioId")
        return usuarioId
    }
}
