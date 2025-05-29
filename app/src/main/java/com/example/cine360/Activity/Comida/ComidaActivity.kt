package com.example.cine360.Activity.Comida

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
import com.example.cine360.Activity.Entrada.EntradaActivity
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Adapter.ComidaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComidaActivity : AppCompatActivity() {

    /*Declaro variables que usaremos para trabjaar con xml, objetos del xml y archivos*/
    private lateinit var recyclerviewComida: RecyclerView
    private lateinit var comidaAdapter: ComidaAdapter
    private lateinit var dbHelper: DataBaseHelper
    private var listaComida: MutableList<Comida> = mutableListOf()
    private lateinit var imageAjustes: ImageView

    /*Funcion que se usa al crear la aplicación*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archiuvo xml sobre el que se va a trabajar en este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comida)

        /*Inicializo una instancia en la base de datos*/
        dbHelper = DataBaseHelper(applicationContext)

        /*Llamo a las variables creadas anteriormnete y les asigno valores o archivos u objetos xml*/
        recyclerviewComida = findViewById(R.id.recyclerViewComida)
        recyclerviewComida.layoutManager = LinearLayoutManager(this)
        listaComida = mutableListOf()
        comidaAdapter = ComidaAdapter(listaComida, this)
        recyclerviewComida.adapter = comidaAdapter
        imageAjustes = findViewById(R.id.imageAjustes)

        /*Llamo a la función para cargar la comida*/
        cargarComida()
        /*Indico que cuando le de a la imagen me meustre el pop up*/
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    /*Funcion para cargar la comida*/
    private fun cargarComida() {
        /*Con corrutinas llamo a la base de datos y sus obbjetos*/
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.readableDatabase
                /*Indico que debe de obtener todas las comidas mediante la función*/
                val comidaList = obtenerComidaDesdeDB(db)
                /*Cierro la conexión con la base de datos*/
                db.close()
                /*Indico que debe de limiar, añadir y actualizar la comida*/
                withContext(Dispatchers.Main) {
                    listaComida.clear()
                    listaComida.addAll(comidaList)
                    comidaAdapter.actualizarComida(listaComida)
                }
            /*En caso de error indico que lo muestre*/
            } catch (e: Exception) {
                Log.e("ComidaActivity", "Error al cargar comida: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComidaActivity, "Failed to load comida", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*Función para obtener todos los datos de comida desde la base de datos*/
    private fun obtenerComidaDesdeDB(db: SQLiteDatabase): List<Comida> {
        /*Creo una lista que contendra todos los datos de comida*/
        val lista = mutableListOf<Comida>()
        try {
            /*Hago un query y le indico que debe llamar a la tabla comida y al array con todos los datos
            * que esta contiene*/
            val cursor = db.query(
                DataBaseHelper.TABLE_COMIDA,
                arrayOf(
                    DataBaseHelper.COLUMN_COMIDA_ID,
                    DataBaseHelper.COLUMN_COMIDA_NOMBRE,
                    DataBaseHelper.COLUMN_COMIDA_DESCRIPCION,
                    DataBaseHelper.COLUMN_COMIDA_PRECIO,
                    DataBaseHelper.COLUMN_COMIDA_IMAGEN
                ),
                null, null, null, null, null
            )
            /*En caso de ser correcto asigno todos los datos a una variable y los añadoi a la lista*/
            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_DESCRIPCION))
                    val precio = it.getDouble(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_PRECIO))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COMIDA_IMAGEN))
                    val comida = Comida(id, nombre, imagen, descripcion, precio)
                    lista.add(comida)
                }
            }
            /*En caos de error muestre un mensaje*/
        } catch (e: Exception) {
            Log.e("ComidaActivity", "Error al obtener comida de la BBDD: ${e.message}")
        }
        return lista
    }

    /*FUnción para mostrar el menu pop up*/
    private fun showPopupMenu(anchorView: View) {
        /*Indico el archivo xml en el cual v a atrabajar la funcion*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)

        val userId = obtenerIdUsuarioActual()
        Log.d("ComidaActivity", "User ID from SharedPreferences for menu: $userId")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Indico que me lleve a la vista de promociones*/
                R.id.menu_promociones -> {
                    startActivity(Intent(this, PromocionesActivity::class.java))
                    true
                }
                /*Indico que me lleve a la vista de peliculas*/
                R.id.menu_peliculas -> {
                    val intent = Intent(this, PeliculaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de comida*/
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java))
                    true
                }
                /*Indico que me lleve a la vista de entradas comida*/
                R.id.menu_entradas_comida -> {
                    val intent = Intent(this, EntradaComidaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de entradas  promociones*/
                R.id.menu_entradas_promociones -> {
                    val intent = Intent(this, EntradaPromocionActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de entradas peliculas*/
                R.id.menu_entradas_peliculas -> {
                    val intent = Intent(this, EntradaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                /*Indico que me lleve a la vista de ajustes de usuario*/
                R.id.menu_usuario -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                /*Indico que cierre sesion*/
                R.id.menu_cerrar_sesion -> {
                    LoginActivity.cerrarSesion(this)
                    true
                }
                else -> false
            }
        }
        /*Muestro el pop up */
        popupMenu.show()
    }

    /*Función para que el usuario solo vea sus entradas al pulsar osbre pop up de forma que llame al usuario y compruebe su id*/
    private fun obtenerIdUsuarioActual(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("ComidaActivity", "No se encontró un usuario logueado en SharedPreferences.")
        }
        Log.d("ComidaActivity", "User ID retrieved from SharedPreferences: $usuarioId")
        return usuarioId
    }
}
