package com.example.cine360.Activity.Entrada

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Adapter.EntradaPromocionAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.EntradaPromociones
import com.example.cine360.MainActivity
import com.example.cine360.R

class EntradaPromocionActivity : AppCompatActivity() {

    private lateinit var recyclerViewEntradasPromociones: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradasPromocionesManager: EntradasPromocionesManager
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_promociones)

        recyclerViewEntradasPromociones = findViewById(R.id.recyclerViewEntradasPromociones)
        btnVolver = findViewById(R.id.btnVolverPromociones)

        recyclerViewEntradasPromociones.layoutManager = LinearLayoutManager(this)

        dbHelper = DataBaseHelper(this)
        entradasPromocionesManager = EntradasPromocionesManager(dbHelper)

        val userId = intent.getIntExtra("USER_ID", -1)

        val entradasPromociones: MutableList<EntradaPromociones> = if (userId != -1) {
            entradasPromocionesManager.obtenerEntradasPromocionesPorUsuario(userId).toMutableList()
        } else {
            entradasPromocionesManager.obtenerTodasLasEntradasPromociones().toMutableList()
        }

        if (entradasPromociones.isEmpty()) {
            Toast.makeText(this, "No has adquirido ninguna promoción", Toast.LENGTH_SHORT).show()
        }

        val adapter = EntradaPromocionAdapter(entradasPromociones, entradasPromocionesManager)
        adapter.setOnEntradaPromocionDeletedListener(object : EntradaPromocionAdapter.OnEntradaPromocionDeletedListener {
            override fun onEntradaPromocionDeleted(entradaPromocion: EntradaPromociones) {
                Log.d(
                    "EntradaPromocionAct",
                    "Entrada de promoción eliminada. ID: ${entradaPromocion.id}, Nombre: ${entradaPromocion.nombrePromocion}"
                )

                Toast.makeText(this@EntradaPromocionActivity, "Promoción eliminada de tu lista", Toast.LENGTH_SHORT).show()
            }
        })
        recyclerViewEntradasPromociones.adapter = adapter

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}