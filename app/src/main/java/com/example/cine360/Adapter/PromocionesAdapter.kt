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
import com.example.cine360.Activity.Entrada.EntradaPromocionActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R
import java.util.Locale

class PromocionesAdapter(
    private var listaPromociones: List<Promociones>,
    private val context: Context
) : RecyclerView.Adapter<PromocionesAdapter.PromocionViewHolder>() {

    private val entradasPromocionesManager: EntradasPromocionesManager by lazy {
        DataBaseHelper(context).let { EntradasPromocionesManager(it) }
    }
    /*Declaramos diferentes variables las cuales obtienes objetos del archivo xml*/
    class PromocionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombrePromocion)
        val descripcionTextView: TextView = itemView.findViewById(R.id.textViewDescripcionProm)
        val precioTextView: TextView = itemView.findViewById(R.id.textviewPrecioProm)
        val comprarButton: Button = itemView.findViewById(R.id.buttonComprarPromocion)
        val imagenPromocion: ImageView = itemView.findViewById(R.id.imagenPromocion)
    }
    /*Obtenemos y indicamos el archivo xml con el cual vamos a trabajar en este archivo*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromocionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promociones, parent, false)
        return PromocionViewHolder(itemView)
    }
    /*Obtenemos la posicion de todas las peliculas a partir de la lista de promociones y asignamos los datos a las diferentes variables
    * anteriormente delcaradas*/
    override fun onBindViewHolder(holder: PromocionViewHolder, position: Int) {
        val promocion = listaPromociones[position]
        holder.nombreTextView.text = promocion.nombre
        holder.descripcionTextView.text = promocion.descripcion
        holder.precioTextView.text = String.format(Locale.getDefault(), "%.2f €", promocion.precio)
        /*Obtenemos las imagenes de las diferentes imagenes de las promociones*/
        val imageName = promocion.imagen
        val resourceId = context.resources.getIdentifier(
            imageName.substringBeforeLast("."),
            "drawable",
            context.packageName
        )

        if (resourceId != 0) {
            Glide.with(holder.itemView.context)
                .load(resourceId)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.error_imagen)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imagenPromocion)
        } else {
            Log.e("PromocionesAdapter", "Resource not found: $imageName")
            holder.imagenPromocion.setImageResource(R.drawable.error_imagen)
        }

        /*Indicamos que si se pulsa el boton debe de obtener el id del usuario y en caso
        * de que este no este registrado se le indica*/
        holder.comprarButton.setOnClickListener {
            val userId = obtenerIdUsuarioActual(holder.itemView.context)

            if (userId == -1) {
                Toast.makeText(
                    holder.itemView.context,
                    "Debes iniciar sesión para adquirir promociones",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            /*Llamos al manager d epromociones y usamos la funcion para crear la entrada
            * y llamamos a los datos de la tabla de la base de datos*/
            val entradaId = entradasPromocionesManager.crearEntrada(
                userId,
                promocion.id,
                promocion.nombre,
                promocion.descripcion,
                promocion.precio,
                promocion.imagen
            )
            /*Realizamos una comprobacion y mostramos si la promociones a sido adquirida y en caso de error se le indica al usuario*/
            if (entradaId > 0) {
                Toast.makeText(holder.itemView.context, "Promoción adquirida", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(holder.itemView.context, EntradaPromocionActivity::class.java)
                intent.putExtra("USER_ID", userId)
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "Error al adquirir la promoción",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount() = listaPromociones.size
    /*Funcion para actualizar las promociones creando una nueva lista y indicandolo en la base de datos*/
    fun actualizarPromociones(nuevaLista: List<Promociones>) {
        listaPromociones = nuevaLista
        notifyDataSetChanged()
    }
    /*Funcion para obtener el id del usuario*/
    private fun obtenerIdUsuarioActual(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)
        if (usuarioId == -1) {
            Log.e("PromocionesAdapter", "No se encontró un usuario logueado")
        }
        return usuarioId
    }
}