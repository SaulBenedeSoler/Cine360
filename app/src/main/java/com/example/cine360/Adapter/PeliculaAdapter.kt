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

        init {

            Glide.with(itemView.context).clear(imagenImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelicula, parent, false)
        return PeliculaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        val pelicula = peliculas[position]
        holder.tituloTextView.text = pelicula.titulo
        holder.descripcionTextView.text = pelicula.descripcion
        holder.semanaTextView.text = pelicula.semana
        holder.generoTextView.text = pelicula.genero
        holder.fechaLanzamientoTextView.text = pelicula.fechaLanzamiento
        holder.duracionTextView.text = pelicula.duracion.toString()

        Log.d("PeliculaAdapter", "Nombre de imagen desde pelicula: ${pelicula.imagen}")
        val imageName = pelicula.imagen.substringBeforeLast(".")
        val resourceId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
        Log.d("PeliculaAdapter", "Resource ID encontrado: $resourceId para imagen: $imageName")
        Log.d("IMAGEN_DEBUG", "Intentando cargar con Glide el resource ID: $resourceId en el ImageView: ${holder.imagenImageView}")
        Glide.with(holder.itemView.context)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.error_imagen)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imagenImageView)


        val dbHelper = DataBaseHelper(context)
        val actorManager = ActorManager(dbHelper)
        val directorManager = DirectorManager(dbHelper)

        val actores = actorManager.obtenerActoresesPorPeliculaId(pelicula.id.toLong())
        val directores = directorManager.obtenerDirectoresPorPeliculaId(pelicula.id.toLong())

        holder.actoresTextView.text = actores.joinToString(", ") { "${it.nombre} ${it.apellido}" }
        holder.directoresTextView.text = directores.joinToString(", ") { "${it.nombre} ${it.apellido}" }

        val userId = obtenerIdUsuarioActual(context)
        Log.d("PeliculaAdapter", "User ID: $userId")

        holder.buttonVerHorarios.setOnClickListener {
            val userId = obtenerIdUsuarioActual(context)
            Log.d("PeliculaAdapter", "Usuario ID al hacer clic en Comprar: $userId")
            val intent = Intent(context, DetallesActivity::class.java)
            intent.putExtra("PELICULA_ID", pelicula.id)
            intent.putExtra("USER_ID", userId)
            context.startActivity(intent)
        }


        Log.d("PeliculaAdapter", "Buscando imagen: $imageName para película: ${pelicula.titulo}")
        Log.d("PeliculaAdapter", "Resource ID encontrado: $resourceId")
    }

    override fun getItemCount() = peliculas.size

    override fun onViewRecycled(holder: PeliculaViewHolder) {
        super.onViewRecycled(holder)
        Log.d("RECYCLER_DEBUG", "ViewHolder reciclado para posición: ${holder.adapterPosition}")
        Glide.with(holder.itemView.context).clear(holder.imagenImageView)
    }

    fun actualizarPeliculas(peliculas: List<Pelicula>) {
        this.peliculas = peliculas
        notifyDataSetChanged()
    }

    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("PeliculaAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}
