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
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.EntradaPromociones
import com.example.cine360.R

class EntradaPromocionAdapter(
    private val entradasPromociones: MutableList<EntradaPromociones>,
    private val entradasPromocionesManager: EntradasPromocionesManager
) : RecyclerView.Adapter<EntradaPromocionAdapter.EntradaPromocionViewHolder>() {

    interface OnEntradaPromocionDeletedListener {
        fun onEntradaPromocionDeleted(entradaPromocion: EntradaPromociones)
    }

    private var listener: OnEntradaPromocionDeletedListener? = null

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


            Glide.with(itemView.context)
                .load(entradaPromocion.imagenPromocion)
                .into(imagenPromocionImageView)

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
}