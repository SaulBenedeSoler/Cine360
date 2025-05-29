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
import com.example.cine360.Adapter.EntradaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradaManager
import com.example.cine360.DataBase.Manager.SalaManager
import com.example.cine360.DataBase.Tablas.Entrada
import com.example.cine360.IndexActivity
import com.example.cine360.R

class EntradaActivity : AppCompatActivity() {

    /*Delcaro variables que usaremos para llamar a archivos xml, objetos ml y arhcivos*/
    private lateinit var recyclerViewEntradas: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradaManager: EntradaManager
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var imageAjustes: ImageView

    /*Función que se ejecuta al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archivo xml sobre el cual va a trabajar la función*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiivty_entrada)

        /*Llamo a las variables anteriormente creadas para asignarle objetos o archivos*/
        recyclerViewEntradas = findViewById(R.id.recyclerViewEntradas)
        btnVolver = findViewById(R.id.btnVolver)
        imageAjustes = findViewById(R.id.imageAjustes)

        recyclerViewEntradas.layoutManager = LinearLayoutManager(this)

        /*Llamada a la base de datos para crear una instancia*/
        dbHelper = DataBaseHelper(this)
        entradaManager = EntradaManager(dbHelper)

        /*Compruebo el id del usuario*/
        val userId = intent.getIntExtra("USER_ID", -1)
        Log.d("EntradaActivity", "User ID received: $userId")

        /*Llam oa la lista de entradas y indico que si no es -1 entonces muestre la lista de las entradas adquiridas por usuario
        * y en caso contrario que meustre todas las entradas adquiridas*/
        val entradas: MutableList<Entrada> = if (userId != -1) {
            entradaManager.obtenerEntradasPorUsuario(userId).toMutableList()
        } else {
            entradaManager.obtenerTodasLasEntradas().toMutableList()
        }

        /*Indico que cuando se pulse sobre la imagen muestre el pop up*/
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }

        /*Indico que si entradas esta vacio debe de mostrar que no tiene entradas y lanzar un mensaje*/
        if (entradas.isEmpty()) {
            Toast.makeText(this, "No hay entradas registradas", Toast.LENGTH_SHORT).show()
        }

        /*Llamo a ladaptador de entradas y indico diferentes acciones*/
        val adapter = EntradaAdapter(this, entradas, entradaManager, dbHelper)
        adapter.setOnEntradaDeletedListener(object : EntradaAdapter.OnEntradaDeletedListener {
            /*Borro la entrada y todos los datos de la base de datos*/
            override fun onEntradaDeleted(entrada: Entrada) {
                Log.d("EntradaActivity", "Entrada eliminada. Película ID: ${entrada.peliculaId}, Asiento: ${entrada.asiento}, Fila: ${entrada.fila}, Horario: ${entrada.horario}, Sala: ${entrada.sala?.nombre}")
                if (entrada.peliculaId != null && entrada.sala?.nombre != null) {
                    entrada.sala?.let {
                        liberarAsiento(
                            it.nombre,
                            entrada.asiento,
                            entrada.fila,
                            entrada.horario,
                            entrada.peliculaId
                        )
                    }
                } else {
                    Log.e("EntradaActivity", "peliculaId es nulo o el nombre de la sala es nulo")
                }
                cargarEntradas()
            }
        })
        /*Llamo al recylcerview del xml*/
        recyclerViewEntradas.adapter = adapter

        /*Indico qeu cuando le de al botón este debe de volver al index*/
        btnVolver.setOnClickListener {
            val intent = Intent(this, IndexActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    /*Función para liberar asientos al eliminar una entrada*/
    private fun liberarAsiento(salaNombre: String, asientoColumna: Int?, fila: Int?, horario: String?, peliculaId: Int) {
        Log.d("EntradaActivity", "Liberando asiento: sala=$salaNombre, asientoColumna=$asientoColumna, fila=$fila, horario=$horario, peliculaId=$peliculaId")
        val salaManager = SalaManager(dbHelper)
        /*Indico que si el horario es nulo muestre un mensaje de error*/
        if (horario == null) {
            Toast.makeText(this, "Error: Horario no especificado", Toast.LENGTH_SHORT).show()
            return
        }
        /*Llamo al manager de sala y obtengo el horario, el id de la pelicula y el nombre de la sala*/
        val salas = salaManager.obtenerSalasPorPeliculaYHorarioYSala(peliculaId, horario, salaNombre)

        /*Indico que si la sala no esta vacia entonces*/
        if (salas.isNotEmpty()) {
            /*Cogo el primer objeto de la sala*/
            val salaObj = salas[0]
            /*COgo el id de la sala*/
            val salaIdLong = salaObj.id
            Log.d("EntradaActivity", "Sala encontrada: salaId=$salaIdLong, nombre=${salaObj.nombre}")
            /*Llamo a la función para obtener los asientos y llamo a la sala correspondiente*/
            val asientosString = salaManager.obtenerAsientos(salaIdLong.toLong())
            /*Si los asientos no son nuloes entonces*/
            if (asientosString != null) {
                /*Loss divido y meto en una lista*/
                val asientos = asientosString.split(",").toMutableList()
                /*Compriuebo que los asientos y la fila no sean nulos*/
                if (asientoColumna != null && fila != null) {
                    /*Indico una variable para que sean 5 columnas*/
                    val numeroDeColumnas = 5
                    /*Calculo el indice de los asientos mediante una operación usando filas y columnas */
                    val asientoIndex = (fila - 1) * numeroDeColumnas + (asientoColumna - 1)
                    /*Indico que si el asiento es mayor a 0 y menor al size de asientos*/
                    if (asientoIndex >= 0 && asientoIndex < asientos.size && asientos[asientoIndex] == "X") {
                        /*Asiento libre*/
                        asientos[asientoIndex] = "O"
                        /*Creo un nuevo asiento*/
                        val nuevoAsientosString = asientos.joinToString(",")
                        /*Llamo a la funcion de actualizar asientos del manager de sala*/
                        salaManager.actualizarAsientos(
                            /*Instancio en la base de datos y inidco que se libere el asiento y cree uno neuvo*/
                            dbHelper.writableDatabase,
                            salaIdLong.toLong(),
                            nuevoAsientosString
                        )
                        /*Indico que se a liberado el asiento*/
                        Toast.makeText(this, "Asiento liberado", Toast.LENGTH_SHORT).show()
                    } else {
                        /*Indico que esta libre o no existe*/
                        Toast.makeText(this, "Asiento no encontrado o ya liberado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error: Información del asiento incompleta", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("EntradaActivity", "No se encontró la sala para la entrada eliminada.")
            Toast.makeText(this, "Error: No se encontró la sala para liberar el asiento", Toast.LENGTH_SHORT).show()
        }
    }

    /*Función para cargar las entradas*/
    private fun cargarEntradas() {
        /*Obtenemos el id del usuario y si no existe asignamos -1*/
        val userId = intent.getIntExtra("USER_ID", -1)
        /*Se cargan todas las entradas en caso de que el id recibido exista en la base de datos*/
        val entradas = if (userId != -1) {
            /*Si existe el id se muestran solo las entradas de ese usuario*/
            entradaManager.obtenerEntradasPorUsuario(userId).toMutableList()
        } else {
            entradaManager.obtenerTodasLasEntradas().toMutableList()
        }
        /*Llamamos al adaptador para mostrar las entradas existentes*/
        val adapter = EntradaAdapter(this, entradas, entradaManager, dbHelper)
        /*Indicamos los pasos a seguir para eliminar una entrada, de forma que al darle al objeto deifinido esta pasara a ser borrada de la base de datos*/
        adapter.setOnEntradaDeletedListener(object : EntradaAdapter.OnEntradaDeletedListener {
            override fun onEntradaDeleted(entrada: Entrada) {
                Log.d("EntradaActivity", "Entrada eliminada. Película ID: ${entrada.peliculaId}, Asiento: ${entrada.asiento}, Fila: ${entrada.fila}, Horario: ${entrada.horario}, Sala: ${entrada.sala?.nombre}")
                if (entrada.peliculaId != null && entrada.sala?.nombre != null) {
                    entrada.sala?.let {
                        liberarAsiento(
                            it.nombre,
                            entrada.asiento,
                            entrada.fila,
                            entrada.horario,
                            entrada.peliculaId
                        )
                    }
                } else {
                    Log.e("EntradaActivity", "peliculaId es nulo o el nombre de la sala es nulo")
                }
                /*Llamamos a la funcion cargar entradas para recargar todas las entradas*/
                cargarEntradas()
            }
        })
        /*Llamamos al recyclerview*/
        recyclerViewEntradas.adapter = adapter
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
