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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.cine360.Activity.Entrada.EntradaComidaActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasComidaManager
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.R
import java.util.Locale

/*Creo una lista que contendra todos los datos de las difernetes comidas*/
class ComidaAdapter(
    private var listaComida: List<Comida>,
    private val valContext: Context
) : RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder>() {

    /*Llamo al manager de comida y le indico que use la abse de datos para obtener las entradas*/
    private val entradasComidaManager: EntradasComidaManager by lazy {
        DataBaseHelper(valContext).let { EntradasComidaManager(it) }
    }
    /*Indico que use el id del usuario para saber quien esta en ese momento*/
    private val currentUserId: Int by lazy {
        obtenerIdUsuarioActual(valContext)
    }

    /*Asigno a variables los diferentes objetos del archivo xml para mostrar los datos*/
    class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombreComida)
        val descripcionTextView: TextView = itemView.findViewById(R.id.textViewDescripcionComida)
        val precioTextView: TextView = itemView.findViewById(R.id.textviewPrecioComida)
        val comprarButton: Button = itemView.findViewById(R.id.buttonComprarComida)
        val imagenComida: ImageView = itemView.findViewById(R.id.imagenComida)
    }
    /*Funcion que indica con que archivo xml vamos a trabajar*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comida, parent, false)
        return ComidaViewHolder(itemView)
    }
    /*LLamo a la lista de comida y le asigno a cada dato una vriable*/
    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {
        val comida = listaComida[position]
        holder.nombreTextView.text = comida.nombre
        holder.descripcionTextView.text = comida.descripcion
        holder.precioTextView.text = String.format(Locale.getDefault(), "%.2f €", comida.precio)

        /*Declaro una variable que usare para obtener los datos de la imagen*/
        val imageName = comida.Imagen
        val resourceId = valContext.resources.getIdentifier(
            imageName.substringBeforeLast("."),
            "drawable",
            valContext.packageName
        )
        /*Muestro la imagen de cada comida*/
        Glide.with(holder.itemView.context)
            .load(resourceId)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.error_imagen)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imagenComida)
        /*Indico que al darle al boton de comprar debera llamar a la actividad de entrada para generar una*/
        holder.comprarButton.setOnClickListener {

            val entradaId = entradasComidaManager.crearEntradaComida(
                currentUserId,
                comida.id,
                comida.nombre,
                comida.descripcion,
                comida.precio,
                comida.Imagen
            )
            if (entradaId > 0) {
                Toast.makeText(holder.itemView.context, "Comida adquirida", Toast.LENGTH_SHORT).show()
                val intent = Intent(holder.itemView.context, EntradaComidaActivity::class.java)
                intent.putExtra("USER_ID", currentUserId)
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Error al adquirir la Comida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*Obtengo todos los datos de la lista*/
    override fun getItemCount() = listaComida.size
    /*Con esta funcion actualizo la lista de comida en caso de introducir cambios*/
    fun actualizarComida(nuevaLista: List<Comida>) {
        listaComida = nuevaLista
        notifyDataSetChanged()
    }

    /*Obtengo que usuario esta realizando las acciones*/
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("PromocionesAdapter", "No se encontró un usuario logueado")
        }

        return usuarioId
    }
}