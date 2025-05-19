package com.example.cine360.Adapter

import android.content.Context
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
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class PeliculaAdminAdapter(
    private val context: Context,
    private val movieList: List<Pelicula>,
    private val onEditClick: (Pelicula) -> Unit,
    private val onDeleteClick: (Pelicula) -> Unit
) : RecyclerView.Adapter<PeliculaAdminAdapter.MovieViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelicula_admin, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = movieList[position]
        holder.textViewTitle.text = "Tiutlo: ${currentMovie.titulo}"
        holder.textViewDescription.text = "Descripción: ${currentMovie.descripcion}"
        holder.textViewGenre.text = "Género: ${currentMovie.genero}"
        holder.textViewReleaseDate.text = "Fecha de lanzamiento: ${currentMovie.fechaLanzamiento}"
        holder.textViewDuration.text = "Duración: ${currentMovie.duracion} minutos"
        holder.textViewWeek.text = "Semana: ${currentMovie.semana}"

        val imageName = currentMovie.imagen.substringBeforeLast(".")
        val resourceId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
        Log.d("PeliculaAdapter", "Resource ID encontrado: $resourceId para imagen: $imageName")
        Log.d("IMAGEN_DEBUG", "Intentando cargar con Glide el resource ID: $resourceId en el ImageView: ${holder.imageViewPelicula}")
        Glide.with(holder.itemView.context)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.error_imagen)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imageViewPelicula)


        holder.buttonEdit.setOnClickListener {
            onEditClick(currentMovie)
        }

        holder.buttonDelete.setOnClickListener {
            onDeleteClick(currentMovie)
        }
    }

    override fun getItemCount() = movieList.size
}