package com.example.cine360.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.cine360.Activity.Pelicula.DetallesActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.ActorManager
import com.example.cine360.DataBase.Manager.DirectorManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class PeliculaAdapter(
    private val context: Context,
    private var peliculas: List<Pelicula>
) : RecyclerView.Adapter<PeliculaAdapter.PeliculaViewHolder>() {

    /*Declaramos diferentes variables las cuales obtienes objetos del archivo xml*/
    class PeliculaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.textViewTituloPelicula)
        val descripcionTextView: TextView = itemView.findViewById(R.id.textViewDescripcion)
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagenPelicula)
        val semanaTextView: TextView = itemView.findViewById(R.id.textViewSemana)
        val actoresTextView: TextView = itemView.findViewById(R.id.textViewActores)
        val directoresTextView: TextView = itemView.findViewById(R.id.textViewDirectorLabel)
        val generoTextView: TextView = itemView.findViewById(R.id.textViewGenero)
        val fechaLanzamientoTextView: TextView = itemView.findViewById(R.id.textViewFechaLanzamiento)
        val duracionTextView: TextView = itemView.findViewById(R.id.textViewDuracion)
        val buttonVerHorarios: Button = itemView.findViewById(R.id.buttonVerHorarios)
        /*Obtenemos la imagen de cada Pelicula*/
        init {
            Glide.with(itemView.context).clear(imagenImageView)
        }
    }
    /*Obtenemos y indicamos el archivo xml con el cual vamos a trabajar en este archivo*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelicula, parent, false)
        return PeliculaViewHolder(itemView)
    }
    /*Obtenemos la posicion de todas las peliculas a partir de la lista de peliculas y asignamos los datos a las diferentes variables
    * anteriormente delcaradas*/
    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        val pelicula = peliculas[position]
        holder.tituloTextView.text = pelicula.titulo
        holder.descripcionTextView.text = pelicula.descripcion
        holder.semanaTextView.text = pelicula.semana
        holder.generoTextView.text = pelicula.genero
        holder.fechaLanzamientoTextView.text = pelicula.fechaLanzamiento

        holder.duracionTextView.text = "${pelicula.duracion} min"

        val imageName = pelicula.imagen.substringBeforeLast(".")
        val resourceId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
        /*Obtenemos las imagenes de las diferentes imagenes de las películas*/
        Glide.with(holder.itemView.context)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.error_imagen)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imagenImageView)

        /*Creamos una instancia con la base de datos, ademas de obtener los datos del manager
        * de la base de datos de actores y directores*/
        val dbHelper = DataBaseHelper(context)
        val actorManager = ActorManager(dbHelper)
        val directorManager = DirectorManager(dbHelper)
        /*Obtenemos desde el manager los directores y actores mediante el id de la pelicula*/
        val actores = actorManager.obtenerActoresesPorPeliculaId(pelicula.id.toLong())
        val directores = directorManager.obtenerDirectoresPorPeliculaId(pelicula.id.toLong())
        /*Obtenemos y mostrarmos el nombre y apellidos de los actores y directores*/
        holder.actoresTextView.text = actores.joinToString(", ") { "${it.nombre} ${it.apellido}" }
        holder.directoresTextView.text = directores.joinToString(", ") { "${it.nombre} ${it.apellido}" }

        val userId = obtenerIdUsuarioActual(context)
        /*Cuando pulsemos el boton indicamos que el usuario sera dirigo a otra vista y obtenemos el id de la pelicula y el usuario*/
        holder.buttonVerHorarios.setOnClickListener {
            val userId = obtenerIdUsuarioActual(context)
            val intent = Intent(context, DetallesActivity::class.java)
            intent.putExtra("PELICULA_ID", pelicula.id)
            intent.putExtra("USER_ID", userId)
            context.startActivity(intent)
        }


    }

    /*Obtenemos la cantidad de pelicuas que tenemos en la base d edatos*/
    override fun getItemCount() = peliculas.size
    /*Obtenemos las imagenes y mediante el  glide limpiamos todas en caso de que se añadan nuevas*/
    override fun onViewRecycled(holder: PeliculaViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.imagenImageView)
    }

    /*Obtenemso el id del usuario*/
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        return usuarioId
    }
}