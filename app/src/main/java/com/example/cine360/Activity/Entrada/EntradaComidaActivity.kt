package com.example.cine360.Activity.Entrada

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Adapter.EntradaComidaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasComidaManager
import com.example.cine360.DataBase.Tablas.EntradaComida
import com.example.cine360.MainActivity
import com.example.cine360.R

class EntradaComidaActivity : AppCompatActivity() {

    private lateinit var recyclerViewEntradasComida: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradasComidaAdapter: EntradaComidaAdapter
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var entradasComidaManager: EntradasComidaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_comida)

        recyclerViewEntradasComida = findViewById(R.id.recyclerViewEntradasComida)
        btnVolver = findViewById(R.id.btnVolverComida)

        recyclerViewEntradasComida.layoutManager = LinearLayoutManager(this)

        dbHelper = DataBaseHelper(this)
        entradasComidaManager = EntradasComidaManager(dbHelper)

        val userId = intent.getIntExtra("USER_ID", -1)

        val entradasComida: MutableList<EntradaComida> = if (userId != -1) {
            entradasComidaManager.obtenerEntradasComidaPorUsuario(userId).toMutableList()
        } else {
            entradasComidaManager.obtenerTodasLasEntradasComida().toMutableList()
        }

        if (entradasComida.isEmpty()) {
            Toast.makeText(this, "No has adquirido ninguna comida", Toast.LENGTH_SHORT).show()
        }

        entradasComidaAdapter = EntradaComidaAdapter(entradasComida, entradasComidaManager)
        entradasComidaAdapter.setOnEntradaComidaDeletedListener(object : EntradaComidaAdapter.OnEntradaComidaDeletedListener {
            override fun onEntradaComidaDeleted(entradaComida: EntradaComida) {
                Log.d(
                    "EntradaComidaActivity",
                    "Entrada de comida eliminada. ID: ${entradaComida.id}, Nombre: ${entradaComida.nombrecomida}"
                )
                Toast.makeText(this@EntradaComidaActivity, "Comida eliminada de tu lista", Toast.LENGTH_SHORT).show()
            }
        })
        recyclerViewEntradasComida.adapter = entradasComidaAdapter

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}

