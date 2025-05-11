package com.example.cine360.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cine360.DataBase.Manager.EntradasComidaManager
import com.example.cine360.DataBase.Tablas.EntradaComida
import com.example.cine360.R

class EntradaComidaAdapter(
    private val entradasComida: MutableList<EntradaComida>,
    private val entradasComidaManager: EntradasComidaManager
) : RecyclerView.Adapter<EntradaComidaAdapter.EntradaComidaViewHolder>() {

    interface OnEntradaComidaDeletedListener {
        fun onEntradaComidaDeleted(entradaComida: EntradaComida)
    }

    private var listener: OnEntradaComidaDeletedListener? = null

    fun setOnEntradaComidaDeletedListener(listener: OnEntradaComidaDeletedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaComidaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entrada_comida, parent, false)
        return EntradaComidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntradaComidaViewHolder, position: Int) {
        val entradaComida = entradasComida[position]
        holder.bind(entradaComida)
    }

    override fun getItemCount(): Int = entradasComida.size

    inner class EntradaComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreComidaTextView: TextView = itemView.findViewById(R.id.tvNombreComida)
        private val descripcionComidaTextView: TextView = itemView.findViewById(R.id.tvDescripcionComida)
        private val precioComidaTextView: TextView = itemView.findViewById(R.id.tvPrecioComida)
        private val imagenComidaImageView: ImageView = itemView.findViewById(R.id.ivImagenComida)
        private val eliminarButton: Button = itemView.findViewById(R.id.btnEliminarEntradaComida)

        fun bind(entradaComida: EntradaComida) {
            nombreComidaTextView.text = "Comida: ${entradaComida.nombrecomida}"
            descripcionComidaTextView.text = entradaComida.descripcioncomida ?: "Sin descripción"
            precioComidaTextView.text = "Precio: ${String.format("%.2f", entradaComida.preciocomida)} €"

            Glide.with(itemView.context)
                .load(entradaComida.imagencomida)
                .into(imagenComidaImageView)

            eliminarButton.setOnClickListener {
                val entradaComidaId = entradaComida.id
                Log.d("EntradaComidaAdapter", "ID de entrada de comida a eliminar: $entradaComidaId")

                val eliminado = entradasComidaManager.eliminarEntradaComida(entradaComidaId)
                if (eliminado) {
                    this@EntradaComidaAdapter.entradasComida.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    listener?.onEntradaComidaDeleted(entradaComida)
                }
            }
        }
    }
}