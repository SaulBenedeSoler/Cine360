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
import com.example.cine360.Adapter.EntradaPromocionAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.EntradaPromociones
import com.example.cine360.IndexActivity
import com.example.cine360.R

class EntradaPromocionActivity : AppCompatActivity() {

    /*Declaamos variables que mas adelante asignaremos a objetos xml o archivos*/
    private lateinit var recyclerViewEntradasPromociones: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradasPromocionesManager: EntradasPromocionesManager
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var imageAjustes: ImageView
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indicamos el objeto xml con el que vamos a trabajar*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_promociones)
        /*Asignamos a las vriables los objetos xml del archivo anteriormnete llamado*/
        recyclerViewEntradasPromociones = findViewById(R.id.recyclerViewEntradasPromociones)
        btnVolver = findViewById(R.id.btnVolverPromociones)
        imageAjustes = findViewById(R.id.imageAjustes)
        context = this
        recyclerViewEntradasPromociones.layoutManager = LinearLayoutManager(this)
        /*Cremos instancia con la base de datos*/
        dbHelper = DataBaseHelper(this)
        entradasPromocionesManager = EntradasPromocionesManager(dbHelper)
        /*Comprobamos el id del usuario*/
        val userId = intent.getIntExtra("USER_ID", -1)
        /*Llamamos a la lista de entradas de promociones y si esta registrado le mostramos sus entradas*/
        val entradasPromociones: MutableList<EntradaPromociones> = if (userId != -1) {
            entradasPromocionesManager.obtenerEntradasPromocionesPorUsuario(userId).toMutableList()
        } else {
            entradasPromocionesManager.obtenerTodasLasEntradasPromociones().toMutableList()
        }
        /*Si el usuario no tiene entradas se muestra mediante un mensaje*/
        if (entradasPromociones.isEmpty()) {
            Toast.makeText(this, "No has adquirido ninguna promoción", Toast.LENGTH_SHORT).show()
        }
        /*Llamamos al adaptador de entradas*/
        val adapter = EntradaPromocionAdapter(context, entradasPromociones, entradasPromocionesManager)
        adapter.setOnEntradaPromocionDeletedListener(object :
            EntradaPromocionAdapter.OnEntradaPromocionDeletedListener {
                /*Borramos las entradas seleccionadas y cargamos las entradas de promociones de nuevo para actualizarlo*/
            override fun onEntradaPromocionDeleted(entradaPromociones: EntradaPromociones) {
                Log.d(
                    "EntradaPromocionAct",
                    "Entrada de promoción eliminada. ID: ${entradaPromociones.id}, Nombre: ${entradaPromociones.nombrePromocion}"
                )

                Toast.makeText(
                    this@EntradaPromocionActivity,
                    "Promoción eliminada de tu lista",
                    Toast.LENGTH_SHORT
                ).show()
                cargarEntradasPromociones()
            }
        })
        recyclerViewEntradasPromociones.adapter = adapter

        /*Si le damos al boton volvemos a la vista anterior*/
        btnVolver.setOnClickListener {
            val intent = Intent(this, IndexActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        /*Si le damos a la imagen nos muestra un menu pop up*/
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }

    }

    /*Funcion para cargar las entradas de promociones*/
    private fun cargarEntradasPromociones() {
        /*Obtenemos el id del usuario*/
        val userId = intent.getIntExtra("USER_ID", -1)
        /*En caso de que exista el id se mostrara la lista con sus entradas*/
        val entradasPromociones: MutableList<EntradaPromociones> = if (userId != -1) {
            entradasPromocionesManager.obtenerEntradasPromocionesPorUsuario(userId).toMutableList()
        } else {
            entradasPromocionesManager.obtenerTodasLasEntradasPromociones().toMutableList()
        }
        /*Si el usuario no tiene ninguna entrada de promocion se mostrara un mensaje informandole de esto*/
        if (entradasPromociones.isEmpty()) {
            Toast.makeText(this, "No has adquirido ninguna promoción", Toast.LENGTH_SHORT).show()
        }
        /*Llamamos al adaptador de entradas y indicamos la funcion para borrar entradas*/
        val adapter = EntradaPromocionAdapter(context, entradasPromociones, entradasPromocionesManager)
        /*Mostramos mensajes indicando si se ha borrado bien la entrada*/
        adapter.setOnEntradaPromocionDeletedListener(object :
            EntradaPromocionAdapter.OnEntradaPromocionDeletedListener {
            override fun onEntradaPromocionDeleted(entradaPromociones: EntradaPromociones) {
                Log.d(
                    "EntradaPromocionAct",
                    "Entrada de promoción eliminada. ID: ${entradaPromociones.id}, Nombre: ${entradaPromociones.nombrePromocion}"
                )

                Toast.makeText(
                    this@EntradaPromocionActivity,
                    "Promoción eliminada de tu lista",
                    Toast.LENGTH_SHORT
                ).show()
                cargarEntradasPromociones()
            }
        })
        recyclerViewEntradasPromociones.adapter = adapter
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
