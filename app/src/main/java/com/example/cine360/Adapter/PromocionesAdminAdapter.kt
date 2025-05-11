package com.example.cine360.Adapter

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R

class PromocionesAdminAdapter(
    private val context: Context,
    private val promocionesList: List<Promociones>,
    private val onEditClick: (Promociones) -> Unit,
    private val onDeleteClick: (Promociones) -> Unit
): RecyclerView.Adapter<PromocionesAdminAdapter.PromocionViewHolder>() {

    inner class PromocionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewTituloPromocion: TextView = itemView.findViewById(R.id.textViewTituloPromocionAmin)
        val imagenViewPromocion: ImageView = itemView.findViewById(R.id.imagenPromocionAdmin) as ImageView
        val textViewDescripcion: TextView = itemView.findViewById(R.id.textViewDescripcionPromocionAdmin)
        val textviewPrecio: TextView = itemView.findViewById(R.id.textViewPrecioPromocion)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEditPromocion)
        val buttonDeletePromocion: Button = itemView.findViewById(R.id.buttonDeletePromocion)
    }


    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int): PromocionViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promociones_admin, parent, false)
        return  PromocionViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: PromocionViewHolder, position:Int){

        val curretnPromocion = promocionesList[position]
        holder.textViewTituloPromocion.text = "Title: ${curretnPromocion.nombre}"
        holder.textViewDescripcion.text = "Title: ${curretnPromocion.descripcion}"
        holder.textviewPrecio.text = "Title: ${curretnPromocion.precio}"


        Glide.with(context)
            .load(curretnPromocion.imagen)
            .into(holder.imagenViewPromocion)

        holder.buttonEdit.setOnClickListener{
            onEditClick(curretnPromocion)
        }

        holder.buttonDeletePromocion.setOnClickListener{
            onDeleteClick(curretnPromocion)
        }

    }


    override fun getItemCount() = promocionesList.size
}