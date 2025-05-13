package com.example.cine360.Activity.Entrada

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Comida.ComidaActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaActivity
import com.example.cine360.Activity.Promociones.PromocionesActivity
import com.example.cine360.Adapter.EntradaComidaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasComidaManager
import com.example.cine360.DataBase.Tablas.EntradaComida
import com.example.cine360.IndexActivity
import com.example.cine360.R

class EntradaComidaActivity : AppCompatActivity() {

    private lateinit var recyclerViewEntradasComida: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradasComidaAdapter: EntradaComidaAdapter
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var entradasComidaManager: EntradasComidaManager
    private lateinit var imageAjustes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_comida)

        recyclerViewEntradasComida = findViewById(R.id.recyclerViewEntradasComida)
        btnVolver = findViewById(R.id.btnVolverComida)
        imageAjustes = findViewById(R.id.imageAjustes)

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
        entradasComidaAdapter.setOnEntradaComidaDeletedListener(object :
            EntradaComidaAdapter.OnEntradaComidaDeletedListener {
            override fun onEntradaComidaDeleted(entradaComida: EntradaComida) {
                Log.d(
                    "EntradaComidaActivity",
                    "Entrada de comida eliminada. ID: ${entradaComida.id}, Nombre: ${entradaComida.nombrecomida}"
                )
                Toast.makeText(
                    this@EntradaComidaActivity,
                    "Comida eliminada de tu lista",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        recyclerViewEntradasComida.adapter = entradasComidaAdapter

        btnVolver.setOnClickListener {
            val intent = Intent(this, IndexActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }

    }


    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_promociones -> {
                    startActivity(Intent(this, PromocionesActivity::class.java))
                    true
                }
                R.id.menu_peliculas -> {
                    startActivity(Intent(this, PeliculaActivity::class.java))
                    true
                }
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java))
                    true
                }

                R.id.menu_entradas_comida -> {
                    startActivity(Intent(this, EntradaComidaActivity::class.java))
                    true
                }
                R.id.menu_entradas_promociones -> {
                    startActivity(Intent(this, EntradaPromocionActivity::class.java))
                    true
                }
                R.id.menu_entradas_peliculas -> {
                    startActivity(Intent(this, EntradaActivity::class.java))
                    true
                }
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java))
                    true
                }

                R.id.menu_usuario -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }

                R.id.menu_cerrar_sesion -> {
                    LoginActivity.cerrarSesion(this)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

}
