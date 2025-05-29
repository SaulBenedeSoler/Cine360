package com.example.cine360.Activity.Promociones

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
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
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.Adapter.PromocionesAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PromocionesActivity : AppCompatActivity() {

    /*Declaro variables que luego asignaremos a archivops o objetos xml*/
    private lateinit var recyclerViewPromociones: RecyclerView
    private lateinit var promocionesAdapter: PromocionesAdapter
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var entradasPromocionesManager: EntradasPromocionesManager
    private var listaPromociones: MutableList<Promociones> = mutableListOf()
    private lateinit var imageAjustes: ImageView

    /*Funcion que se usa el iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archiuvo xml sobre el que se va a trabajar en este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promocion)
        /*Inicializo una instancia en la base de datos*/
        dbHelper = DataBaseHelper(applicationContext)
        /*Llamo a las variables creadas anteriormnete y les asigno valores o archivos u objetos xml*/
        entradasPromocionesManager = EntradasPromocionesManager(dbHelper)
        recyclerViewPromociones = findViewById(R.id.recyclerViewPromociones)
        recyclerViewPromociones.layoutManager = LinearLayoutManager(this)
        promocionesAdapter = PromocionesAdapter(emptyList(), this)
        recyclerViewPromociones.adapter = promocionesAdapter
        /*Indico que cuando le de a la imagen me meustre el pop up*/
        imageAjustes = findViewById(R.id.imageAjustes)
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
        /*Llamo a la funcion para cargar las promociones*/
        cargarPromociones()
    }
    /*Funcion para cargar las promociones*/
    private fun cargarPromociones() {
        /*Mediante corrutinas obtengo los datos de la tabla de promociones*/
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.readableDatabase
                val promocionList = obtenerPromocionesDesdeDB(db)
                db.close()
                withContext(Dispatchers.Main) {
                    /*Limpio toda la lista de promociones, leugo añado toda la lista
                    * para ver si ahi cambios y para finalizar
                    * llamo a la funcion de actualizar la lista que se encuenta en el
                    * promocionesAdapter*/
                    listaPromociones.clear()
                    listaPromociones.addAll(promocionList)
                    promocionesAdapter.actualizarPromociones(listaPromociones)
                }

            } catch (e: Exception) {
                Log.e("PromocionesActivity", "Error al cargar promociones: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PromocionesActivity, "Failed to load promotions", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    /*Funcion para obtener todas las promociones de la base de datos*/
    private fun obtenerPromocionesDesdeDB(db: SQLiteDatabase): List<Promociones> {
       /*Creo una lista de promociones y obtengo toods los datos de la tabla*/
        val lista = mutableListOf<Promociones>()
        try {
            val cursor = db.query(
                DataBaseHelper.TABLE_PROMOCIONES,
                arrayOf(
                    DataBaseHelper.COLUMN_PROMOCION_ID,
                    DataBaseHelper.COLUMN_PROMOCION_NOMBRE,
                    DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION,
                    DataBaseHelper.COLUMN_PROMOCION_IMAGEN,
                    DataBaseHelper.COLUMN_PROMOCION_PRECIO
                ),
                null, null, null, null, null
            )
            /*Asigno variables a cada datos de la tabla y lo añado a la lista*/
            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_NOMBRE))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_IMAGEN))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_DESCRIPCION))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PROMOCION_PRECIO))
                    val promocion = Promociones(id, nombre, imagen, descripcion , precio)
                    lista.add(promocion)
                }
            }
        } catch (e: Exception) {
            Log.e("PromocionesActivity", "Error al obtener promociones de la BBDD: ${e.message}")
        }
        return lista
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