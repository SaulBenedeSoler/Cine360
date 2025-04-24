package com.example.cine360.Activity.LoginYRegister

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Adapter.UserAdapter
import com.example.cine360.DataBase.Manager.UserManager
import com.example.cine360.DataBase.Tablas.Usuario
import com.example.cine360.R

class AdminActivity : AppCompatActivity() {

    private lateinit var userManager: UserManager
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: MutableList<Usuario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val username = intent.getStringExtra("USERNAME") ?: "Admin"
        title = "Panel de Administrador - $username"

        userManager = UserManager(this)
        userList = userManager.getAllUsers().toMutableList()

        val lvUsers = findViewById<ListView>(R.id.lvUsers)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        userAdapter = UserAdapter(this, userList)
        lvUsers.adapter = userAdapter

        lvUsers.setOnItemClickListener { _, _, position, _ ->
            val user = userList[position]
            showUserOptions(user, position)
        }

        btnLogout.setOnClickListener {
            finish()
        }
    }

    private fun showUserOptions(user: Usuario, position: Int) {
        val options = arrayOf("Editar", "Eliminar")

        AlertDialog.Builder(this)
            .setTitle("Opciones para: ${user.username}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(user, position)
                    1 -> showDeleteDialog(user, position)
                }
            }
            .show()
    }

    private fun showEditDialog(user: Usuario, position: Int) {
        Toast.makeText(this, "Función de edición de usuario", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteDialog(user: Usuario, position: Int) {
        if (user.username == "admin") {
            Toast.makeText(this, "No se puede eliminar el administrador por defecto", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Está seguro de que desea eliminar a ${user.username}?")
            .setPositiveButton("Eliminar") { _, _ ->
                val result = userManager.deleteUser(user.id)
                if (result > 0) {
                    userList.removeAt(position)
                    userAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshUserList()
    }

    private fun refreshUserList() {
        userList.clear()
        userList.addAll(userManager.getAllUsers())
        userAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        userManager.close()
    }
}