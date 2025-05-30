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
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.R

class ComidaAdminAdapter(
    private val context: Context,
    private val comidaList: List<Comida>,
    private val onEditClick: (Comida) -> Unit,
    private val onDeleteClick: (Comida) -> Unit
) : RecyclerView.Adapter<ComidaAdminAdapter.ComidaViewHolder>() {

    private val currentUserId: Int by lazy {
        obtenerIdUsuarioActual(context)
    }

    /*Asigno a cada variable un objeto xml*/
    inner class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTituloComidaAmin: TextView = itemView.findViewById(R.id.textViewTituloComidaAmin)
        val textViewDescripcionComidaAdmin: TextView = itemView.findViewById(R.id.textViewDescripcionComidaAdmin)
        val textviewPrecioComida: TextView = itemView.findViewById(R.id.textViewPrecioPromocion)
        val imagenComidaAdmin: ImageView = itemView.findViewById(R.id.imagenComidaAdmin)
        val buttonEditComida: Button = itemView.findViewById(R.id.buttonEditComida)
        val buttonDeleteComida: Button = itemView.findViewById(R.id.buttonDeleteComida)

        init {
            Glide.with(itemView.context).clear(imagenComidaAdmin)
        }
    }
    /*Indico el archivo xml sobre el que vamos a trabajar*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comida_admin, parent, false)
        return ComidaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {
        val currentComida = comidaList[position]
        Log.d("ComidaAdminAdapter", "Procesando comida en posición $position: ${currentComida.nombre}")
        /*Asigno a cada variable el dato de la base de datos corresoponiende*/
        holder.textViewTituloComidaAmin.text = currentComida.nombre
        holder.textViewDescripcionComidaAdmin.text = "Descripción: ${currentComida.descripcion}"
        holder.textviewPrecioComida.text = "Precio: ${currentComida.precio.toString()}"
        /*Obtengo las imagenes de la base de datos*/
        val imageName = currentComida.Imagen
        val resourceId = context.resources.getIdentifier(
            imageName.substringBeforeLast("."),
            "drawable",
            context.packageName
        )
        /*Muestro la imagen correspondiente a cada comida*/
        Glide.with(holder.itemView.context)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.error_imagen)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imagenComidaAdmin)
        /*Al pulsar sobre el vamos a la vista para modificar la comida*/
        holder.buttonEditComida.setOnClickListener {
            onEditClick(currentComida)
        }
        /*Al pulsar sobre el eliminamos la comida seleccionada*/
        holder.buttonDeleteComida.setOnClickListener {
            onDeleteClick(currentComida)
        }
    }

    override fun getItemCount() = comidaList.size
    /*Muestro la imgen seleccionada y la limpio en caso de nuevas imagenes*/
    override fun onViewRecycled(holder: ComidaViewHolder) {
        super.onViewRecycled(holder)
        Log.d("RECYCLER_DEBUG", "ViewHolder reciclado para posición: ${holder.adapterPosition}")
        Glide.with(holder.itemView.context).clear(holder.imagenComidaAdmin)
    }
    /*Obtengo el id de usuario para verificar quie nes*/
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            Log.e("ComidaAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}