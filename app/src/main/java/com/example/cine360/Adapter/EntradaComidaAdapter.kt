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
import com.example.cine360.DataBase.Manager.EntradasComidaManager
import com.example.cine360.DataBase.Tablas.EntradaComida
import com.example.cine360.R

class EntradaComidaAdapter(
    private val context: Context,
    private val entradasComida: MutableList<EntradaComida>,
    private val entradasComidaManager: EntradasComidaManager
) : RecyclerView.Adapter<EntradaComidaAdapter.EntradaComidaViewHolder>() {

    interface OnEntradaComidaDeletedListener {
        fun onEntradaComidaDeleted(entradaComida: EntradaComida)
    }

    private var listener: OnEntradaComidaDeletedListener? = null
    private val currentUserId: Int by lazy {
        obtenerIdUsuarioActual(context)
    }

    fun setOnEntradaComidaDeletedListener(listener: OnEntradaComidaDeletedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaComidaViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_entrada_comida, parent, false)
        return EntradaComidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntradaComidaViewHolder, position: Int) {
        val entradaComida = entradasComida[position]
        holder.bind(entradaComida)
    }

    override fun getItemCount(): Int = entradasComida.size

    inner class EntradaComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreComidaTextView: TextView = itemView.findViewById(R.id.tvNombreComida)
        private val descripcionComidaTextView: TextView =
            itemView.findViewById(R.id.tvDescripcionComida)
        private val precioComidaTextView: TextView = itemView.findViewById(R.id.tvPrecioComida)
        private val imagenComidaImageView: ImageView = itemView.findViewById(R.id.ivImagenComida)
        private val eliminarButton: Button = itemView.findViewById(R.id.btnEliminarEntradaComida)

        fun bind(entradaComida: EntradaComida) {
            nombreComidaTextView.text = "Comida: ${entradaComida.nombrecomida}"
            descripcionComidaTextView.text = entradaComida.descripcioncomida ?: "Sin descripción"
            precioComidaTextView.text =
                "Precio: ${String.format("%.2f", entradaComida.preciocomida)} €"

            val imageName = entradaComida.imagencomida
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
                    .into(imagenComidaImageView)
            } else {
                Log.e("EntradaPromocionAdapter", "Resource not found: $imageName")
                imagenComidaImageView.setImageResource(R.drawable.error_imagen)
            }

            eliminarButton.setOnClickListener {
                val entradaComidaId = entradaComida.id
                Log.d(
                    "EntradaComidaAdapter",
                    "ID de entrada de comida a eliminar: $entradaComidaId"
                )

                val eliminado = entradasComidaManager.eliminarEntradaComida(entradaComidaId)
                if (eliminado) {
                    this@EntradaComidaAdapter.entradasComida.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    listener?.onEntradaComidaDeleted(entradaComida)
                }
            }
        }
    }
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            Log.e("EntradaComidaAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}