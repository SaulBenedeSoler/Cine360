package com.example.cine360.Activity.Entrada

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.Adapter.EntradaComidaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasComidaManager
import com.example.cine360.DataBase.Tablas.EntradaComida
import com.example.cine360.IndexActivity
import com.example.cine360.R

class EntradaComidaActivity : AppCompatActivity() {

    /*Definimos las variables y les asignamos objetos xml o archivos*/
    private lateinit var recyclerViewEntradasComida: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradasComidaAdapter: EntradaComidaAdapter
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var entradasComidaManager: EntradasComidaManager
    private lateinit var imageAjustes: ImageView
    private var userId: Int = -1
    private lateinit var context: Context

    /*Función que se usa al crear la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Definimos el archivo xml en el cual vamos a trabjar con este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_comida)

        /*Llamamos a las variables anteriormente creadas y les asignamos los objetos xml del archivo xml anteriormente indicado
        * y también asignamos archivos.*/
        recyclerViewEntradasComida = findViewById(R.id.recyclerViewComida)
        imageAjustes = findViewById(R.id.imageAjustes)

        btnVolver = findViewById(R.id.btnVolverComida)

        context = this

        recyclerViewEntradasComida.layoutManager = LinearLayoutManager(this)

        /*Creamos una instancia con la base de datos*/
        dbHelper = DataBaseHelper(this)
        entradasComidaManager = EntradasComidaManager(dbHelper)

        /*Obtenemos el id del usuario*/
        userId = intent.getIntExtra("USER_ID", -1)
        Log.d("EntradaComidaActivity", "User ID received in onCreate: $userId")

        /*Llamamos a la funcion con la cual cargaremos toda la comida de la base de datos*/
        cargarEntradasComida()

        /*Indicamos que al pulsar sobre la imagen hara la accion de mostrar el pop up*/
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
        /*Indicamos que al pulsar sobre el boton realizara la acción indicada*/
        btnVolver.setOnClickListener {
            val intent = Intent(this, IndexActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    /*Funcion para cargar todos los datos de la tabla comida de la base de datos*/
    private fun cargarEntradasComida() {
        /*Verificamos que el usuario exista para mostrarle las entradas que este a adquirido*/
        if (userId != -1) {
            val entradasComida = entradasComidaManager.obtenerEntradasComidaPorUsuario(userId).toMutableList()

            /*Si no se ha adquirido ninguna entrada se mostrara un mensaje*/
            if (entradasComida.isEmpty()) {
                Toast.makeText(this, "No has adquirido ninguna comida", Toast.LENGTH_SHORT).show()
            }
            /*Llamamos al adaptador de este para indicarle que coga los datos y realizamos la función de borrar entrada*/
            entradasComidaAdapter = EntradaComidaAdapter(context, entradasComida, entradasComidaManager)
            entradasComidaAdapter.setOnEntradaComidaDeletedListener(object :
                EntradaComidaAdapter.OnEntradaComidaDeletedListener {
                /*Funcion para eliminar entradas*/
                override fun onEntradaComidaDeleted(entradaComida: EntradaComida) {
                    /*Eliminamos las entradas de comida*/
                    Log.d(
                        "EntradaComidaActivity",
                        "Entrada de comida eliminada. ID: ${entradaComida.id}, Nombre: ${entradaComida.nombrecomida}"
                    )
                    Toast.makeText(
                        this@EntradaComidaActivity,
                        "Comida eliminada de tu lista",
                        Toast.LENGTH_SHORT
                    ).show()
                    /*Cargamos de nuevo todas las entradas restantes*/
                    cargarEntradasComida()
                }
            })
            recyclerViewEntradasComida.adapter = entradasComidaAdapter
        } else {
            Toast.makeText(this, "Error: No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT)
                .show()
            Log.e("EntradaComidaActivity", "No USER_ID found, finishing activity.")
            finish()
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
        Log.d("EntradaActivity", "User ID from SharedPreferences for menu: $userId")

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

    /*Funcion para comprobar el id del usuario*/
    private fun obtenerIdUsuarioActual(): Int {
        /*Accedemos a las preferencias del archivo xml y obtenemos el id del usuario*/
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        /*Si el id del usuario no existe se mostrara un mensaje de error*/
        if (usuarioId == -1) {
            Log.e("EntradaActivity", "No se encontró un usuario logueado en SharedPreferences.")
        }
        Log.d("EntradaActivity", "User ID retrieved from SharedPreferences: $usuarioId")
        return usuarioId
    }
}