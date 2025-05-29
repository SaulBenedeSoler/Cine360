package com.example.cine360.Activity.Pelicula

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
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
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.Adapter.PeliculaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class PeliculaActivity : AppCompatActivity() {

    /*Declaro las variables que usaremos para asignar archivos xml o otros archivos*/
    private lateinit var peliculaManager: PeliculaManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAjustes: ImageView

    /*Funcion que se usa al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico sobre que archivo xml vamos a trabjar*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peliculas)

       /*Obtengo la semana seleccionada*/
        val semanaSeleccionada = intent.getStringExtra("SEMANA") ?: "Semana desconocida"
        Log.d("PeliculaActivity", "Semana seleccionada: $semanaSeleccionada")
        /*Creo una isntancia de la base de datos y la utilizo en el manager*/
        val dbHelper = DataBaseHelper(this)
        val db = dbHelper.readableDatabase
        peliculaManager = PeliculaManager(dbHelper)
        /*Asigno a las variables anteriormente delcaradas su respectivo objeto xml*/
        recyclerView = findViewById(R.id.recyclerViewPeliculas)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        /*Indico que al pulsar sobre le boton se muestre el menu pop up*/
        imageAjustes = findViewById(R.id.imageAjustes)
        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }
        /*Obtengo todas las peliculas por id de la semana*/
        val semanaId = obtenerIdSemana(semanaSeleccionada, db)
        Log.d("PeliculaActivity", "Semana ID: $semanaId")

        val peliculas = obtenerPeliculasPorSemana(db, semanaId)

        Log.d("PeliculaActivity", "Películas encontradas: ${peliculas.size}")

        val adapter = PeliculaAdapter(this, peliculas)
        recyclerView.adapter = adapter
    }

    /*Funcion para obtener las peliculas por semana*/
    private fun obtenerPeliculasPorSemana(db: SQLiteDatabase, semanaId: Int): List<Pelicula> {
       /*Creo una lista de peliculas*/
        val peliculas = mutableListOf<Pelicula>()
        try {
            val semanaString = (semanaId + 1).toString()
            /*Realizo una sentencia para coger todos los datos de la tabla pelicula filtrado por la columna semana*/
            val query =
                "SELECT * FROM ${DataBaseHelper.TABLE_PELICULA} WHERE ${DataBaseHelper.COLUMN_SEMANA} = ?"
            db.rawQuery(query, arrayOf(semanaString)).use { cursor ->
                Log.d("PeliculaActivity", "Cursor count: ${cursor.count}")

                if (cursor.moveToFirst()) {
                    do {
                        /*Llamo a los objetos del archivo gestion de la base de datos y le asigno los datos de la tabla de la base de datos*/
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_Pelicula_ID))
                        val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUM_TITULO))
                        val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPCION))
                        val genero = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_GENERO))
                        val fechaLanzamiento = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO))
                        val duracion = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DURACION))
                        val imagen = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_IMAGEN))
                        val trailer = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_TRAILER))
                        val semana = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA))

                       /*Llamo a la tabla de peliculas y añado toods los datos*/
                        val pelicula = Pelicula(
                            id.toInt(),
                            titulo,
                            descripcion,
                            genero,
                            fechaLanzamiento,
                            duracion,
                            imagen,
                            trailer,
                            semana
                        )
                        peliculas.add(pelicula)

                        Log.d("PeliculaActivity", "Película añadida: $titulo")
                    } while (cursor.moveToNext())
                } else {
                    Log.d("PeliculaActivity", "No se encontraron películas para esta semana")
                }
            }
        } catch (e: Exception) {
            Log.e("PeliculaActivity", "Error al obtener películas por semana: ${e.message}")
        }

        return peliculas
    }

    /*Obtengo el id de la semana que corresponde a cada pelicula*/
    private fun obtenerIdSemana(nombreSemana: String, db: SQLiteDatabase): Int {
        val semanaRegex = "Semana (\\d+)".toRegex()
        val matchResult = semanaRegex.find(nombreSemana)

        return if (matchResult != null) {
            val numero = matchResult.groupValues[1].toIntOrNull() ?: 1
            numero - 1
        } else {
            0
        }
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