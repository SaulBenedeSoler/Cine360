package com.example.cine360.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class PeliculaAdminAdapter(
    private val context: Context,
    private val movieList: List<Pelicula>,
    private val onEditClick: (Pelicula) -> Unit,
    private val onDeleteClick: (Pelicula) -> Unit
) : RecyclerView.Adapter<PeliculaAdminAdapter.MovieViewHolder>() {

    /*Declaramos las diferentes variables y les asignamos los objetos xml*/
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTituloPelicula)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescripcion)
        val textViewGenre: TextView = itemView.findViewById(R.id.textViewGenero)
        val textViewReleaseDate: TextView = itemView.findViewById(R.id.textViewFechaLanzamiento)
        val textViewDuration: TextView = itemView.findViewById(R.id.textViewDuracion)
        val imageViewPelicula: ImageView = itemView.findViewById(R.id.imagenPelicula)
        val textViewWeek: TextView = itemView.findViewById(R.id.textViewSemana)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEditMovie)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDeleteMovie)
        init {

            Glide.with(itemView.context).clear(imageViewPelicula)
        }
    }

    /*Indicamos el objeto xml con el cual vamos a trabajar en este archivo y de esta forma podremos asignar diferentes
    * objetos de este*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelicula_admin, parent, false)
        return MovieViewHolder(itemView)
    }

    /*Llamamos a las variables anteriormente declaradas y le pasamos los datos de la base de datos*/
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = movieList[position]
        holder.textViewTitle.text = "Tiutlo: ${currentMovie.titulo}"
        holder.textViewDescription.text = "Descripción: ${currentMovie.descripcion}"
        holder.textViewGenre.text = "Género: ${currentMovie.genero}"
        holder.textViewReleaseDate.text = "Fecha de lanzamiento: ${currentMovie.fechaLanzamiento}"
        holder.textViewDuration.text = "Duración: ${currentMovie.duracion} minutos"
        holder.textViewWeek.text = "Semana: ${currentMovie.semana}"
        /*Obtenemos las imagenes de la base de datos*/
        val imageName = currentMovie.imagen.substringBeforeLast(".")
        val resourceId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
        /*Usamos el glide para mostrar las imagenes llamando los objetos xml*/
        Glide.with(holder.itemView.context)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.error_imagen)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imageViewPelicula)

        /*Indicamos que cuando se pulse el boton nos lleve a editar la pelicula*/
        holder.buttonEdit.setOnClickListener {
            onEditClick(currentMovie)
        }
        /*Indicamos que si se pulsa el boton elimine la pelicula*/
        holder.buttonDelete.setOnClickListener {
            onDeleteClick(currentMovie)
        }
    }

    override fun getItemCount() = movieList.size
}