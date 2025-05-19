package com.example.cine360.Activity.Entrada

import android.content.Context
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
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.Adapter.EntradaPromocionAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradasPromocionesManager
import com.example.cine360.DataBase.Tablas.EntradaPromociones
import com.example.cine360.IndexActivity
import com.example.cine360.R

class EntradaPromocionActivity : AppCompatActivity() {

    private lateinit var recyclerViewEntradasPromociones: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradasPromocionesManager: EntradasPromocionesManager
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var imageAjustes: ImageView
    private lateinit var context: Context // Added context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_promociones)

        recyclerViewEntradasPromociones = findViewById(R.id.recyclerViewEntradasPromociones)
        btnVolver = findViewById(R.id.btnVolverPromociones)
        imageAjustes = findViewById(R.id.imageAjustes)
        context = this // Initialize context

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

        val adapter = EntradaPromocionAdapter(context, entradasPromociones, entradasPromocionesManager)
        adapter.setOnEntradaPromocionDeletedListener(object :
            EntradaPromocionAdapter.OnEntradaPromocionDeletedListener {
            override fun onEntradaPromocionDeleted(entradaPromociones: EntradaPromociones) {
                Log.d(
                    "EntradaPromocionAct",
                    "Entrada de promoción eliminada. ID: ${entradaPromociones.id}, Nombre: ${entradaPromociones.nombrePromocion}"
                )

                Toast.makeText(
                    this@EntradaPromocionActivity,
                    "Promoción eliminada de tu lista",
                    Toast.LENGTH_SHORT
                ).show()
                cargarEntradasPromociones()
            }
        })
        recyclerViewEntradasPromociones.adapter = adapter

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

    private fun cargarEntradasPromociones() {
        val userId = intent.getIntExtra("USER_ID", -1)
        val entradasPromociones: MutableList<EntradaPromociones> = if (userId != -1) {
            entradasPromocionesManager.obtenerEntradasPromocionesPorUsuario(userId).toMutableList()
        } else {
            entradasPromocionesManager.obtenerTodasLasEntradasPromociones().toMutableList()
        }

        if (entradasPromociones.isEmpty()) {
            Toast.makeText(this, "No has adquirido ninguna promoción", Toast.LENGTH_SHORT).show()
        }
        val adapter = EntradaPromocionAdapter(context, entradasPromociones, entradasPromocionesManager)
        adapter.setOnEntradaPromocionDeletedListener(object :
            EntradaPromocionAdapter.OnEntradaPromocionDeletedListener {
            override fun onEntradaPromocionDeleted(entradaPromociones: EntradaPromociones) {
                Log.d(
                    "EntradaPromocionAct",
                    "Entrada de promoción eliminada. ID: ${entradaPromociones.id}, Nombre: ${entradaPromociones.nombrePromocion}"
                )

                Toast.makeText(
                    this@EntradaPromocionActivity,
                    "Promoción eliminada de tu lista",
                    Toast.LENGTH_SHORT
                ).show()
                cargarEntradasPromociones()
            }
        })
        recyclerViewEntradasPromociones.adapter = adapter
    }

    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_activity, popupMenu.menu)


        val userId = obtenerIdUsuarioActual()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_promociones -> {
                    startActivity(Intent(this, PromocionesActivity::class.java))
                    true
                }
                R.id.menu_peliculas -> {
                    startActivity(Intent(this, SemanaActivity::class.java))
                    true
                }
                R.id.menu_comida -> {
                    startActivity(Intent(this, ComidaActivity::class.java))
                    true
                }

                R.id.menu_entradas_comida -> {
                    val intent = Intent(this, EntradaComidaActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    true
                }
                R.id.menu_entradas_promociones -> {
                    val intent = Intent(this, EntradaPromocionActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
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

    private fun obtenerIdUsuarioActual(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPreferences.getInt("usuario_id", -1)

        if (usuarioId == -1) {
            Log.e("NombreDeTuActividad", "No se encontró un usuario logueado")

        }
        Log.d("obtenerIdUsuarioActual", "El id es $usuarioId")
        return usuarioId
    }



}
