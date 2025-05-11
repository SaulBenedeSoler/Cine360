package com.example.cine360.Activity.Comida

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Promociones.AñadirEditarPromocionAdminActivity
import com.example.cine360.Adapter.ComidaAdapter
import com.example.cine360.Adapter.ComidaAdminAdapter
import com.example.cine360.DataBase.Manager.ComidaAdminManager
import com.example.cine360.DataBase.Manager.PromocionesAdminManager
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ComidaAdminActivity : AppCompatActivity() {
    private lateinit var recyclerviewComida: RecyclerView
    private lateinit var adapter: ComidaAdminAdapter
    private lateinit var comidaAdminManager: ComidaAdminManager
    private lateinit var fabAddComida: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_comida)

        recyclerviewComida = findViewById(R.id.recyclerviewComida)
        fabAddComida = findViewById(R.id.fabAddComida)
        recyclerviewComida.layoutManager = LinearLayoutManager(this)

        comidaAdminManager = ComidaAdminManager(this)

        loadComida()

        fabAddComida.setOnClickListener {
            val intent = Intent(this, AñadirEditarComidaAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadComida() {
        val comidaList = comidaAdminManager.getAllComida()
        adapter = ComidaAdminAdapter(this, comidaList,
            onEditClick = { comida ->
                val intent = Intent(this, AñadirEditarComidaAdminActivity::class.java)
                intent.putExtra("COMIDA_ID", comida.id)
                startActivity(intent)
            },
            onDeleteClick = { comida ->
                confirmDeleteComida(comida)
            }
        )
        recyclerviewComida.adapter = adapter
    }

    private fun confirmDeleteComida(comida: Comida) {
        val rowsDeleted = comidaAdminManager.deleteComida(comida.id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "${comida.nombre} deleted", Toast.LENGTH_SHORT).show()
            loadComida()
        } else {
            Toast.makeText(this, "Error deleting ${comida.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        comidaAdminManager.close()
    }
}