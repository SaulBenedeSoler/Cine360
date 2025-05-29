package com.example.cine360.Adapter

import android.content.Context
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
    private val context: Context,
    private val entradas: MutableList<Entrada>,
    private val entradaManager: EntradaManager,
    private val dbHelper: DataBaseHelper
) : RecyclerView.Adapter<EntradaAdapter.EntradaViewHolder>() {

    /*Indico para eliminar la entrada*/
    interface OnEntradaDeletedListener {
        fun onEntradaDeleted(entrada: Entrada)
    }
    /*Referencio el listener y el id del usuario*/
    private var listener: OnEntradaDeletedListener? = null
    private val currentUserId: Int by lazy {
        obtenerIdUsuarioActual(context)
    }

    fun setOnEntradaDeletedListener(listener: OnEntradaDeletedListener) {
        this.listener = listener
    }
    /*Indico el archivo xml en el cual se va a trabajar*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entrada, parent, false)
        return EntradaViewHolder(view)
    }
    /*Funcion para obtener todas las entradas y la posicion en la que se encuentran*/
    override fun onBindViewHolder(holder: EntradaViewHolder, position: Int) {
        val entrada = entradas[position]
        holder.bind(entrada)
    }

    /*Funcion para obtener las entradas*/
    override fun getItemCount(): Int = entradas.size
    /*Se asigna a cada variable el objeto xml que debe contener*/
    inner class EntradaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val peliculaTextView: TextView = itemView.findViewById(R.id.tvPeliculaNombre)
        private val salaTextView: TextView = itemView.findViewById(R.id.tvSala)
        private val asientoTextView: TextView = itemView.findViewById(R.id.tvAsiento)
        private val horarioTextView: TextView = itemView.findViewById(R.id.tvHorario)
        private val eliminarButton: Button = itemView.findViewById(R.id.btnEliminarEntrada)
        /*Se obtienen los datos de la base de datos necesarios*/
        fun bind(entrada: Entrada) {
            peliculaTextView.text = "Película: ${entrada.nombrePelicula}"
            salaTextView.text = "Sala: ${entrada.sala?.nombre ?: "Desconocida"}"
            asientoTextView.text = "Asiento: ${entrada.asiento}, Fila: ${entrada.fila}"
            horarioTextView.text = "Horario: ${entrada.horario}"


            Log.d("EntradaAdapter", "User ID: $currentUserId")
            /*Cuando se pulse el boton se llamara a la funcion que se encuentra en el manager y se eliminara la entrada*/
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
    /*Funcion para comprobar que usuario es y asignarle correctamente su entrada*/
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("EntradaAdapter", "No se encontró un usuario logueado")
        }

        return usuarioId
    }
}