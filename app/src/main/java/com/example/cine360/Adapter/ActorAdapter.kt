package com.example.cine360.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.DataBase.Tablas.Actor
import com.example.cine360.R

class ActorAdapter(private val actores: List<Actor>) :
    RecyclerView.Adapter<ActorAdapter.ActorViewHolder>() {

    class ActorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreActorTextView: TextView = itemView.findViewById(R.id.textViewActores)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelicula, parent, false)
        return ActorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor = actores[position]
        holder.nombreActorTextView.text = "${actor.nombre} ${actor.apellido}"
    }

    override fun getItemCount() = actores.size
}