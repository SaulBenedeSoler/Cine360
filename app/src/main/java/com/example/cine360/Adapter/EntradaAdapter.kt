package com.example.cine360.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradaManager
import com.example.cine360.DataBase.Tablas.Entrada
import com.example.cine360.R

class EntradaAdapter(
    private val entradas: MutableList<Entrada>,
    private val entradaManager: EntradaManager,
    private val dbHelper: DataBaseHelper
) : RecyclerView.Adapter<EntradaAdapter.EntradaViewHolder>() {

    interface OnEntradaDeletedListener {
        fun onEntradaDeleted(entrada: Entrada)
    }

    private var listener: OnEntradaDeletedListener? = null

    fun setOnEntradaDeletedListener(listener: OnEntradaDeletedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entrada, parent, false)
        return EntradaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntradaViewHolder, position: Int) {
        val entrada = entradas[position]
        holder.bind(entrada)
    }

    override fun getItemCount(): Int = entradas.size

    inner class EntradaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val peliculaTextView: TextView = itemView.findViewById(R.id.tvPeliculaNombre)
        private val salaTextView: TextView = itemView.findViewById(R.id.tvSala)
        private val asientoTextView: TextView = itemView.findViewById(R.id.tvAsiento)
        private val horarioTextView: TextView = itemView.findViewById(R.id.tvHorario)
        private val eliminarButton: Button = itemView.findViewById(R.id.btnEliminarEntrada)

        fun bind(entrada: Entrada) {
            peliculaTextView.text = "Película: ${entrada.nombrePelicula}"
            salaTextView.text = "Sala: ${entrada.sala?.nombre ?: "Desconocida"}"
            asientoTextView.text = "Asiento: ${entrada.asiento}, Fila: ${entrada.fila}"
            horarioTextView.text = "Horario: ${entrada.horario}"

            eliminarButton.setOnClickListener {
                val entradaId = entrada.id
                Log.d("EntradaAdapter", "ID de entrada a eliminar: $entradaId")

                val eliminado = entradaManager.eliminarEntrada(entradaId)
                if (eliminado) {
                    this@EntradaAdapter.entradas.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    listener?.onEntradaDeleted(entrada)
                }

            }
        }
    }
}