package com.example.cine360.Adapter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class SemanaAdapter(
    private val context: Context,
    private val semanas: List<String>,
    private val peliculaManager: PeliculaManager,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SemanaAdapter.ViewHolder>() {

    /*Declaramos las varibales que usaremos más adelante*/
    private val expandedState = mutableMapOf<Int, Boolean>()
    private val dbHelper = peliculaManager.dbHelper
    private val db = dbHelper.readableDatabase
    private val currentUserId: Int by lazy {
        obtenerIdUsuarioActual(context)
    }

    /*Asignamos variables a diferentes objetos xml*/
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSemana: TextView = view.findViewById(R.id.textViewSemana)
        val imageExpandCollapse: ImageView = view.findViewById(R.id.imageExpandCollapse)
        val layoutHeader: LinearLayout = view.findViewById(R.id.layoutHeaderSemana)
        val recyclerViewPeliculas: RecyclerView = view.findViewById(R.id.recyclerViewPeliculasDeSemana)
    }

    /*Indicamos el archivo xml con el cual vamos a trabajr y obtener los objetos necesarios*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_semana, parent, false)
        return ViewHolder(view)
    }
    /*Obtenemos la funcion de la pelicula y le pasamos a un objetoxml lso datos de la base de datos de la semana*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val semana = semanas[position]
        holder.textViewSemana.text = semana
        /*Indicamos un desplegable para mostrar las peliculas por semanas*/
        val isExpanded = expandedState[position] ?: false
        holder.imageExpandCollapse.setImageResource(
            if (isExpanded) R.drawable.ic_colap_more else R.drawable.ic_expand_more
        )
        holder.recyclerViewPeliculas.visibility = if (isExpanded) View.VISIBLE else View.GONE
        if (isExpanded && holder.recyclerViewPeliculas.adapter == null) {
            cargarPeliculasDeSemana(holder, semana)
        }
        holder.layoutHeader.setOnClickListener {
            val previousExpandedState = expandedState[position] ?: false
            expandedState[position] = !previousExpandedState

            notifyItemChanged(position)
        }

        holder.itemView.setOnClickListener { onItemClick(semana) }
    }

    private fun cargarPeliculasDeSemana(holder: ViewHolder, semana: String) {

        /*Declaramos el objeto xml con el cual vamos a trabajar*/
        val horizontalLayoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerViewPeliculas.layoutManager = horizontalLayoutManager

        /*Obtenemos las semanas por su id*/
        val semanaId = obtenerIdSemana(semana, db)

        /*Obtenemos todas las peliculas de la base de datos por semana mediante la base de datos y el id de la semana*/
        val peliculas = obtenerPeliculasPorSemana(db, semanaId)

        /*Llamos al adapter d epeliculas y lo asignamos a un objeto del xml*/
        val adapter = PeliculaAdapter(holder.itemView.context, peliculas)
        holder.recyclerViewPeliculas.adapter = adapter
    }

    /*Funcion para obtener las peliculas por semanas*/
    private fun obtenerPeliculasPorSemana(db: SQLiteDatabase, semanaId: Int): List<Pelicula> {
        /*Creamos una lista con los datos de la Pleicula*/
        val peliculas = mutableListOf<Pelicula>()

        try {

            val semanaString = (semanaId + 1).toString()
            /*Realizamos uan consulta query y llamamos a la tabla de pelicula y mediante la declaracion de variables
            * obtenemos toods los datos del archivo DataBaseHelper el cual usamos para guardar y obtener los datos de la base de datos*/
            val query = "SELECT * FROM ${DataBaseHelper.TABLE_PELICULA} WHERE ${DataBaseHelper.COLUMN_SEMANA} = ?"
            db.rawQuery(query, arrayOf(semanaString)).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_Pelicula_ID))
                        val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUM_TITULO))
                        val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPCION))
                        val genero = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_GENERO))
                        val fechaLanzamiento = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO))
                        val duracion = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DURACION))
                        val imagen = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_IMAGEN))
                        val trailer = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_TRAILER))
                        val semana = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA))

                        val pelicula = Pelicula(id.toInt(), titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana)
                        peliculas.add(pelicula)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            Log.e("SemanaAdapter", "Error al obtener películas por semana: ${e.message}")
        }

        return peliculas
    }
    /*Ibtenemos el id de las semanas*/
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

    override fun getItemCount() = semanas.size

    /*Obtenemos el id del usuario para saber quien esta realizando acciones*/
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            Log.e("PromocionesAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}