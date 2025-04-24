package com.example.cine360.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cine360.Activity.Pelicula.DetallesActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.ActorManager
import com.example.cine360.DataBase.Manager.DirectorManager
import com.example.cine360.DataBase.Tablas.Pelicula
import com.example.cine360.R

class PeliculaAdapter(private val context: Context, private var peliculas: List<Pelicula>) :
    RecyclerView.Adapter<PeliculaAdapter.PeliculaViewHolder>() {

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pelicula, parent, false)
        return PeliculaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        val pelicula = peliculas[position]
        holder.tituloTextView.text = pelicula.titulo
        holder.descripcionTextView.text = pelicula.descripcion
        holder.semanaTextView.text = pelicula.semana
        holder.generoTextView.text = pelicula.genero
        holder.fechaLanzamientoTextView.text = "Fecha de lanzamiento: ${pelicula.fechaLanzamiento}"
        holder.duracionTextView.text = "Duración: ${pelicula.duracion} minutos"


        val imageName = pelicula.imagen.substringBeforeLast(".")
        val resourceId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )

        Glide.with(context)
            .load(resourceId)
            .into(holder.imagenImageView)

        val dbHelper = DataBaseHelper(context)
        val actorManager = ActorManager(dbHelper)
        val directorManager = DirectorManager(dbHelper)

        val actores = actorManager.obtenerActoresesPorPeliculaId(pelicula.id.toLong())
        val directores = directorManager.obtenerDirectoresPorPeliculaId(pelicula.id.toLong())


        holder.actoresTextView.text = actores.joinToString(", ") { "${it.nombre} ${it.apellido}" }
        holder.directoresTextView.text = directores.joinToString(", ") { "${it.nombre} ${it.apellido}" }

        holder.buttonVerHorarios.setOnClickListener {
            val intent = Intent(context, DetallesActivity::class.java)
            intent.putExtra("PELICULA_ID", pelicula.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = peliculas.size

    fun actualizarPeliculas(peliculas: List<Pelicula>) {
        this.peliculas = peliculas
        notifyDataSetChanged()
    }
}