package com.example.cine360.Activity.Promociones

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Adapter.PromocionesAdminAdapter // Corrected import
import com.example.cine360.DataBase.Manager.PromocionesAdminManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class PromocionesAdminActivity : AppCompatActivity() {

    private lateinit var recyclerViewPromociones: RecyclerView
    private lateinit var adapter: PromocionesAdminAdapter // Corrected type
    private lateinit var PromocionesAdminManager: PromocionesAdminManager
    private lateinit var fabAdPromociones: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_promocion)

        recyclerViewPromociones = findViewById(R.id.recyclerViewPromociones)
        fabAdPromociones = findViewById(R.id.fabAddPromociones)
        recyclerViewPromociones.layoutManager = LinearLayoutManager(this)

        PromocionesAdminManager = PromocionesAdminManager(this)

        loadPromociones()

        fabAdPromociones.setOnClickListener {
            val intent = Intent(this, AñadirEditarPromocionAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadPromociones() {
        val promocionesList = PromocionesAdminManager.getAllPromociones()
        adapter = PromocionesAdminAdapter(this, promocionesList,
            onEditClick = { Promociones ->
                val intent = Intent(this, AñadirEditarPromocionAdminActivity::class.java)
                intent.putExtra("Promocion_id", Promociones.id)
                startActivity(intent)
            },
            onDeleteClick = { Promociones ->
                confirmDeletePromociones(Promociones)
            }
        )
        recyclerViewPromociones.adapter = adapter
    }

    private fun confirmDeletePromociones(promocion: Promociones) {
        val rowsDeleted = PromocionesAdminManager.deletePromocion(promocion.id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "${promocion.nombre} deleted", Toast.LENGTH_SHORT).show()
            loadPromociones()
        } else {
            Toast.makeText(this, "Error deleting ${promocion.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PromocionesAdminManager.close()
    }
}
