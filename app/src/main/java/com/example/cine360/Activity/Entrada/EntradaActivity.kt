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
import com.example.cine360.Adapter.EntradaAdapter
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Manager.EntradaManager
import com.example.cine360.DataBase.Manager.SalaManager
import com.example.cine360.DataBase.Tablas.Entrada
import com.example.cine360.IndexActivity
import com.example.cine360.R

class EntradaActivity : AppCompatActivity() {

    private lateinit var recyclerViewEntradas: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var entradaManager: EntradaManager
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var imageAjustes: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiivty_entrada)

        recyclerViewEntradas = findViewById(R.id.recyclerViewEntradas)
        btnVolver = findViewById(R.id.btnVolver)
        imageAjustes = findViewById(R.id.imageAjustes)

        recyclerViewEntradas.layoutManager = LinearLayoutManager(this)

        dbHelper = DataBaseHelper(this)
        entradaManager = EntradaManager(dbHelper)

        val userId = intent.getIntExtra("USER_ID", -1)

        val entradas: MutableList<Entrada> = if (userId != -1) {
            entradaManager.obtenerEntradasPorUsuario(userId).toMutableList()
        } else {
            entradaManager.obtenerTodasLasEntradas().toMutableList()
        }

        imageAjustes.setOnClickListener { view ->
            showPopupMenu(view)
        }


        if (entradas.isEmpty()) {
            Toast.makeText(this, "No hay entradas registradas", Toast.LENGTH_SHORT).show()
        }

        val adapter = EntradaAdapter(this, entradas, entradaManager, dbHelper) // Pass context
        adapter.setOnEntradaDeletedListener(object : EntradaAdapter.OnEntradaDeletedListener {
            override fun onEntradaDeleted(entrada: Entrada) {
                Log.d("EntradaActivity", "Entrada eliminada. Película ID: ${entrada.peliculaId}, Asiento: ${entrada.asiento}, Fila: ${entrada.fila}, Horario: ${entrada.horario}, Sala: ${entrada.sala?.nombre}")
                if (entrada.peliculaId != null && entrada.sala?.nombre != null) {
                    entrada.sala?.let {
                        liberarAsiento(
                            it.nombre,
                            entrada.asiento,
                            entrada.fila,
                            entrada.horario,
                            entrada.peliculaId
                        )
                    }
                } else {
                    Log.e("EntradaActivity", "peliculaId es nulo o el nombre de la sala es nulo")
                }
                cargarEntradas() // Reload the list after deletion
            }
        })
        recyclerViewEntradas.adapter = adapter

        btnVolver.setOnClickListener {
            val intent = Intent(this, IndexActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun liberarAsiento(salaNombre: String, asientoColumna: Int?, fila: Int?, horario: String?, peliculaId: Int) {
        Log.d(
            "EntradaActivity",
            "Liberando asiento: sala=$salaNombre, asientoColumna=$asientoColumna, fila=$fila, horario=$horario, peliculaId=$peliculaId"
        )
        val salaManager = SalaManager(dbHelper)

        if (horario == null) {
            Toast.makeText(this, "Error: Horario no especificado", Toast.LENGTH_SHORT).show()
            return
        }


        val salas = salaManager.obtenerSalasPorPeliculaYHorarioYSala(peliculaId, horario, salaNombre)

        if (salas.isNotEmpty()) {
            val salaObj = salas[0]
            val salaIdLong = salaObj.id
            Log.d("EntradaActivity", "Sala encontrada: salaId=$salaIdLong, nombre=${salaObj.nombre}")
            val asientosString = salaManager.obtenerAsientos(salaIdLong.toLong())
            if (asientosString != null) {
                val asientos = asientosString.split(",").toMutableList()
                if (asientoColumna != null && fila != null) {
                    val numeroDeColumnas = 5 // Assuming 5 columns, adjust as necessary
                    val asientoIndex = (fila - 1) * numeroDeColumnas + (asientoColumna - 1)

                    if (asientoIndex >= 0 && asientoIndex < asientos.size && asientos[asientoIndex] == "X") {
                        asientos[asientoIndex] = "O"
                        val nuevoAsientosString = asientos.joinToString(",")
                        salaManager.actualizarAsientos(
                            dbHelper.writableDatabase,
                            salaIdLong.toLong(),
                            nuevoAsientosString
                        )
                        Toast.makeText(this, "Asiento liberado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Asiento no encontrado o ya liberado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error: Información del asiento incompleta", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("EntradaActivity", "No se encontró la sala para la entrada eliminada.")
            Toast.makeText(this, "Error: No se encontró la sala para liberar el asiento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarEntradas() {
        val userId = intent.getIntExtra("USER_ID", -1)
        val entradas = if (userId != -1) {
            entradaManager.obtenerEntradasPorUsuario(userId).toMutableList()
        } else {
            entradaManager.obtenerTodasLasEntradas().toMutableList()
        }
        val adapter = EntradaAdapter(this, entradas, entradaManager, dbHelper) // Pass context
        adapter.setOnEntradaDeletedListener(object : EntradaAdapter.OnEntradaDeletedListener {
            override fun onEntradaDeleted(entrada: Entrada) {
                Log.d("EntradaActivity", "Entrada eliminada. Película ID: ${entrada.peliculaId}, Asiento: ${entrada.asiento}, Fila: ${entrada.fila}, Horario: ${entrada.horario}, Sala: ${entrada.sala?.nombre}")
                if (entrada.peliculaId != null && entrada.sala?.nombre != null) {
                    entrada.sala?.let {
                        liberarAsiento(
                            it.nombre,
                            entrada.asiento,
                            entrada.fila,
                            entrada.horario,
                            entrada.peliculaId
                        )
                    }
                } else {
                    Log.e("EntradaActivity", "peliculaId es nulo o el nombre de la sala es nulo")
                }
                cargarEntradas()
            }
        })
        recyclerViewEntradas.adapter = adapter
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
