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
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R

class PromocionesAdminAdapter(
    private val context: Context,
    private val promocionesList: List<Promociones>,
    private val onEditClick: (Promociones) -> Unit,
    private val onDeleteClick: (Promociones) -> Unit
) : RecyclerView.Adapter<PromocionesAdminAdapter.PromocionesViewHolder>() {

    private val currentUserId: Int by lazy {
        obtenerIdUsuarioActual(context)
    }
    /*Declaramos las diferentes variables y les asignamos los objetos xml*/
    inner class PromocionesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombrePromociones: TextView? = itemView.findViewById(R.id.textViewNombrePromocion)
        val textViewDescripcionPromociones: TextView? = itemView.findViewById(R.id.textViewDescripcionProm)
        val textViewPrecioPromocion: TextView? = itemView.findViewById(R.id.textviewPrecioProm)
        val imagenPromociones: ImageView? = itemView.findViewById(R.id.imagenPromocion)
        val buttonEditPromociones: Button? = itemView.findViewById(R.id.buttonEditPromocion)
        val buttonDeletePromociones: Button? = itemView.findViewById(R.id.buttonDeletePromocion)


    }
    /*Indicamos el objeto xml con el cual vamos a trabajar en este archivo y de esta forma podremos asignar diferentes
       * objetos de este*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromocionesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promociones_admin, parent, false)
        return PromocionesViewHolder(itemView)
    }
    /*Llamamos a las variables anteriormente declaradas y le pasamos los datos de la base de datos*/
    override fun onBindViewHolder(holder: PromocionesViewHolder, position: Int) {
        val currentPromocion = promocionesList[position]
        Log.d("PromocionAdminAdapter", "Procesando promocion en posición $position: ${currentPromocion.nombre}")
        holder.textViewNombrePromociones?.text = currentPromocion.nombre
        holder.textViewDescripcionPromociones?.text = " ${currentPromocion.descripcion}"
        holder.textViewPrecioPromocion?.text = " ${currentPromocion.precio.toString()} €"
        /*Obtenemos las imagenes de la base de datos*/
        val imageName = currentPromocion.imagen
        val resourceId = context.resources.getIdentifier(
            imageName.substringBeforeLast("."),
            "drawable",
            context.packageName
        )
        /*Usamos el glide para mostrar las imagenes llamando los objetos xml*/
        if (resourceId != 0) {
            holder.imagenPromociones?.let {
                Glide.with(holder.itemView.context)
                    .load(resourceId)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.error_imagen)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(it)
            }
        } else {
            Log.e("PromocionesAdminAdapter", "Resource not found for image: $imageName")
            holder.imagenPromociones?.setImageResource(R.drawable.error_imagen)
        }
        /*Indicamos que cuando se pulse el boton nos lleve a editar la pelicula*/
        holder.buttonEditPromociones?.setOnClickListener {
            onEditClick(currentPromocion)
        }
        /*Indicamos que si se pulsa el boton elimine la pelicula*/
        holder.buttonDeletePromociones?.setOnClickListener {
            onDeleteClick(currentPromocion)
        }
    }
    /*Obtenemos el tamaño de la lista de las promociones*/
    override fun getItemCount() = promocionesList.size

    /*Obtenemos las imagenes y las limpiamos en caso de que se añadan nuevas*/
    override fun onViewRecycled(holder: PromocionesViewHolder) {
        super.onViewRecycled(holder)
        holder.imagenPromociones?.let{
            Glide.with(holder.itemView.context).clear(it)
        }
    }
    /*Indicamos el id del usuario*/
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            Log.e("PromocionAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}