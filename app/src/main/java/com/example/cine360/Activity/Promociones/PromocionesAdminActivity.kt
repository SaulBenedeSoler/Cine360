package com.example.cine360.Activity.Promociones

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Adapter.PromocionesAdminAdapter
import com.example.cine360.DataBase.Manager.PromocionesAdminManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PromocionesAdminActivity : AppCompatActivity() {
    private lateinit var recyclerViewPromociones: RecyclerView
    private lateinit var adapter: PromocionesAdminAdapter
    private lateinit var promocionesAdminManager: PromocionesAdminManager
    private lateinit var fabAddPromocion: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_promocion)

        recyclerViewPromociones = findViewById(R.id.recyclerViewPromociones)
        fabAddPromocion = findViewById(R.id.fabAddPromociones)
        recyclerViewPromociones.layoutManager = LinearLayoutManager(this)

        promocionesAdminManager = PromocionesAdminManager(this)

        loadPromociones()

        fabAddPromocion.setOnClickListener {
            val intent = Intent(this, AñadirEditarPromocionAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadPromociones() {
        val promocionList = promocionesAdminManager.getAllPromociones()
        adapter = PromocionesAdminAdapter(this, promocionList,
            onEditClick = { promocion ->
                val intent = Intent(this, AñadirEditarPromocionAdminActivity::class.java)
                intent.putExtra("PROMOCION_ID", promocion.id)
                startActivity(intent)
            },
            onDeleteClick = { promocion ->
                confirmDeleteProm(promocion)
            }
        )
        recyclerViewPromociones.adapter = adapter
    }

    private fun confirmDeleteProm(promocion: Promociones) {
        val rowsDeleted = promocionesAdminManager.deletePromociones(promocion.id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "${promocion.nombre} deleted", Toast.LENGTH_SHORT).show()
            loadPromociones()
        } else {
            Toast.makeText(this, "Error deleting ${promocion.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        promocionesAdminManager.close()
    }
}