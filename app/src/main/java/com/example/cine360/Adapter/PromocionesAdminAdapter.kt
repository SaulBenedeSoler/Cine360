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

    inner class PromocionesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombrePromociones: TextView? = itemView.findViewById(R.id.textViewNombrePromocion) // Make them nullable
        val textViewDescripcionPromociones: TextView? = itemView.findViewById(R.id.textViewDescripcionProm)
        val textViewPrecioPromocion: TextView? = itemView.findViewById(R.id.textViewPrecioPromocion)
        val imagenPromociones: ImageView? = itemView.findViewById(R.id.imagenPromocion)
        val buttonEditPromociones: Button? = itemView.findViewById(R.id.buttonEditPromocion)
        val buttonDeletePromociones: Button? = itemView.findViewById(R.id.buttonDeletePromocion)

        init {
            // Glide.with(itemView.context).clear(imagenPromociones)  // Removed from here
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromocionesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promociones_admin, parent, false)
        return PromocionesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PromocionesViewHolder, position: Int) {
        val currentPromocion = promocionesList[position]
        Log.d("PromocionAdminAdapter", "Procesando promocion en posición $position: ${currentPromocion.nombre}")

        // Use the null safety operator ?. to avoid NullPointerExceptions.
        holder.textViewNombrePromociones?.text = currentPromocion.nombre
        holder.textViewDescripcionPromociones?.text = "Descripción: ${currentPromocion.descripcion}"
        holder.textViewPrecioPromocion?.text = "Precio: ${currentPromocion.precio.toString()}"

        val imageName = currentPromocion.imagen
        val resourceId = context.resources.getIdentifier(
            imageName.substringBeforeLast("."),
            "drawable",
            context.packageName
        )

        if (resourceId != 0) {
            holder.imagenPromociones?.let { // Use holder.imagenPromociones?.let
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

        holder.buttonEditPromociones?.setOnClickListener {
            onEditClick(currentPromocion)
        }

        holder.buttonDeletePromociones?.setOnClickListener {
            onDeleteClick(currentPromocion)
        }
    }

    override fun getItemCount() = promocionesList.size


    override fun onViewRecycled(holder: PromocionesViewHolder) {
        super.onViewRecycled(holder)
        Log.d("RECYCLER_DEBUG", "ViewHolder reciclado para posición: ${holder.adapterPosition}")
        holder.imagenPromociones?.let{
            Glide.with(holder.itemView.context).clear(it)
        }
    }

    private fun obtenerIdUsuarioActual(context: Context): Int { //ADDED THIS
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            Log.e("PromocionAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}
