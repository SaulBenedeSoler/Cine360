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
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.EntradaPromociones
import com.example.cine360.R

class EntradaPromocionAdapter(
    private val context: Context, // Added context parameter
    private val entradasPromociones: MutableList<EntradaPromociones>,
    private val entradasPromocionesManager: EntradasPromocionesManager
) : RecyclerView.Adapter<EntradaPromocionAdapter.EntradaPromocionViewHolder>() {

    interface OnEntradaPromocionDeletedListener {
        fun onEntradaPromocionDeleted(entradaPromociones: EntradaPromociones)
    }

    private var listener: OnEntradaPromocionDeletedListener? = null
    private val currentUserId: Int by lazy {  //ADDED THIS
        obtenerIdUsuarioActual(context)
    }

    fun setOnEntradaPromocionDeletedListener(listener: OnEntradaPromocionDeletedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaPromocionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entrada_promocion, parent, false)
        return EntradaPromocionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntradaPromocionViewHolder, position: Int) {
        val entradaPromocion = entradasPromociones[position]
        holder.bind(entradaPromocion)
    }

    override fun getItemCount(): Int = entradasPromociones.size

    inner class EntradaPromocionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombrePromocionTextView: TextView = itemView.findViewById(R.id.tvNombrePromocion)
        private val descripcionPromocionTextView: TextView = itemView.findViewById(R.id.tvDescripcionPromocion)
        private val precioPromocionTextView: TextView = itemView.findViewById(R.id.tvPrecioPromocion)
        private val imagenPromocionImageView: ImageView = itemView.findViewById(R.id.ivImagenPromocion)
        private val eliminarButton: Button = itemView.findViewById(R.id.btnEliminarEntradaPromocion)

        fun bind(entradaPromocion: EntradaPromociones) {
            nombrePromocionTextView.text = "Promoción: ${entradaPromocion.nombrePromocion}"
            descripcionPromocionTextView.text =
                entradaPromocion.descripcionPromocion ?: "Sin descripción"
            precioPromocionTextView.text =
                "Precio: ${String.format("%.2f", entradaPromocion.precioPromocion)} €"

            val imageName = entradaPromocion.imagenPromocion
            val resourceId = if (imageName != null) {
                itemView.context.resources.getIdentifier(
                    imageName.substringBeforeLast("."),
                    "drawable",
                    itemView.context.packageName
                )
            } else {
                0
            }

            if (resourceId != 0) {
                Glide.with(itemView.context)
                    .load(resourceId)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.error_imagen)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imagenPromocionImageView)
            } else {
                Log.e("EntradaPromocionAdapter", "Resource not found: $imageName")
                imagenPromocionImageView.setImageResource(R.drawable.error_imagen)
            }

            eliminarButton.setOnClickListener {
                val entradaPromocionId = entradaPromocion.id
                Log.d(
                    "EntradaPromocionAdapter",
                    "ID de entrada de promoción a eliminar: $entradaPromocionId"
                )

                val rowsAffected =
                    entradasPromocionesManager.eliminarEntradaPromocion(entradaPromocionId)
                if (rowsAffected > 0) {

                    this@EntradaPromocionAdapter.entradasPromociones.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    listener?.onEntradaPromocionDeleted(entradaPromocion)
                }
            }
        }
    }

    private fun obtenerIdUsuarioActual(context: Context): Int { //ADDED THIS
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            Log.e("EntradaPromocionAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}
