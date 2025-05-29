package com.example.cine360.Activity.Semana

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Entrada.EntradaActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Adapter.SemanaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Manager.SemanaManager
import com.example.cine360.R

class SemanaActivity : AppCompatActivity() {

    /*Declaro las variables necesarias para realizar las actividades*/
    private lateinit var imageAjustes: ImageView
    private lateinit var context: Context
    /*Funcion que se usa al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Declaro el archivo xml con el que se trabajara*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semanas)

        /*Creo una instancia con la base de datos*/
        context = this
        val dbHelper = DataBaseHelper(context)
        val db = dbHelper.readableDatabase
        val semanaManager = SemanaManager(dbHelper)
        /*Asigno los objetos xml del archivo anteriormente seleccionado a variables*/
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSemanas)
        recyclerView.layoutManager = LinearLayoutManager(context)
        /*Llamo al manager para obtener todas las semanas*/
        val semanas = semanaManager.obtenerTodasLasSemanas()
        /*Indico como mostrar las semanas*/
        val semanasParaMostrar = if (semanas.isEmpty()) {
            listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")
        } else {
            semanas
        }
        /*Uso el manager con la base de datos*/
        val peliculaManager = PeliculaManager(dbHelper)
        /*Asigno una variable a un objeto xml y indico que al pulsarlo meustre el menu pop up*/
        imageAjustes = findViewById(R.id.imageAjustes)

        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
        /*Uso el adapter para qeu cuando seleccione una semana use la funcion de iraPeliculas y muestre peliculas asociadas*/
        val adapter = SemanaAdapter(context, semanasParaMostrar, peliculaManager) { semanaSeleccionada ->
            irAPeliculas(semanaSeleccionada)
        }
        recyclerView.adapter = adapter
    }

    /*Funcion para ver las peliculas asignadas a cada semana*/
    private fun irAPeliculas(semana: String) {
        val intent = Intent(context, PeliculaActivity::class.java)
        intent.putExtra("SEMANA", semana)
        val userId = obtenerIdUsuarioActual()
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
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