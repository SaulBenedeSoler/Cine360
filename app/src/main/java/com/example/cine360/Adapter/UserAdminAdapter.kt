package com.example.cine360.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.R
import com.example.cine360.DataBase.Tablas.Usuario

class UserAdminAdapter(
    private val context: Context,
    private val userList: List<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UserAdminAdapter.UserViewHolder>() {

    /*Declaramos variables y las asignamos a los diferentes objetos del archivo xml seleccionado*/
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val textViewEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val textViewRole: TextView = itemView.findViewById(R.id.tvRole)
        val buttonEditUser: Button = itemView.findViewById(R.id.buttonEditUser)
        val buttonDeleteUser: Button = itemView.findViewById(R.id.buttonDeleteUser)
    }
    /*Indicamos con que archivo xml vamos a trabajar para obtener los nombres de sus objetos*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }
    /*Obtenemos la posicion de la lista de todos los usuarios y llamamos a los objetos xml del archivo
    * para indicar en la base de datos sus datos*/
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textViewUsername.text = currentUser.username
        holder.textViewEmail.text = currentUser.email

        /*Comprobamos si el usuario es administrador o no*/
        if (currentUser.isAdmin) {
            holder.textViewRole.text = "administrador"
        } else {
            holder.textViewRole.text = "usuario"
        }

        /*Si le damos al boton nos lleva a editar el usuario seleccionado*/
        holder.buttonEditUser.setOnClickListener {
            onEditClick(currentUser)
        }
        /*Si pulsamos el booton elimina al usuario de la base de datos*/
        holder.buttonDeleteUser.setOnClickListener {
            onDeleteClick(currentUser)
        }
    }

    override fun getItemCount() = userList.size
}