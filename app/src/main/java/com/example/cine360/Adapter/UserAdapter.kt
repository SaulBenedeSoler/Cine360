package com.example.cine360.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cine360.DataBase.Tablas.Usuario
import com.example.cine360.R

class UserAdapter(context: Context, private val users: List<Usuario>) :

    ArrayAdapter<Usuario>(context, R.layout.item_user, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

        val user = users[position]

        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvRole = view.findViewById<TextView>(R.id.tvRole)

        tvUsername.text = user.username
        tvEmail.text = user.email
        tvRole.text = if (user.isAdmin) "Administrador" else "Usuario"

        return view
    }
}