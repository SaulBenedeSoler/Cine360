package com.example.cine360.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R

class ComidaAdminAdapter (
    private val context: Context,
    private val comidaList: List<Comida>,
    private val onEditClick: (Comida) -> Unit,
    private val onDeleteClick: (Comida) -> Unit
): RecyclerView.Adapter<ComidaAdminAdapter.ComidaViewHolder>() {

    inner class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textviewNombreComida: TextView = itemView.findViewById(R.id.textViewNombreComida)
        val imagenViewComida: ImageView = itemView.findViewById(R.id.imagenComidaAdmin) as ImageView
        val textViewDescripcion: TextView = itemView.findViewById(R.id.textViewDescripcionComidaAdmin)
        val textviewPrecio: TextView = itemView.findViewById(R.id.textviewPrecioComida)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEditComida)
        val buttonDeleteComida: Button = itemView.findViewById(R.id.buttonDeleteComida)
    }


    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int): ComidaViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promociones_admin, parent, false)
        return  ComidaViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ComidaViewHolder, position:Int){

        val currentComida = comidaList[position]
        holder.textviewNombreComida.text = "Title: ${currentComida.nombre}"
        holder.textViewDescripcion.text = "Title: ${currentComida.descripcion}"
        holder.textviewPrecio.text = "Title: ${currentComida.precio}"


        Glide.with(context)
            .load(currentComida.Imagen)
            .into(holder.imagenViewComida)

        holder.buttonEdit.setOnClickListener{
            onEditClick(currentComida)
        }

        holder.buttonDeleteComida.setOnClickListener{
            onDeleteClick(currentComida)
        }

    }


    override fun getItemCount() = comidaList.size
}