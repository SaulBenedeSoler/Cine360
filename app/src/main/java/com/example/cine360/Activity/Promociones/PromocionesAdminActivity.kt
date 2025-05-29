package com.example.cine360.Activity.Promociones

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cine360.Activity.Comida.ComidaAdminActivity
import com.example.cine360.Activity.LoginYRegister.AdminActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.Adapter.PromocionesAdminAdapter
import com.example.cine360.DataBase.Manager.PromocionesAdminManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class PromocionesAdminActivity : AppCompatActivity() {

    /*Declaro variables que luego seran asignadas a objetos xml o otros archivos*/
    private lateinit var recyclerViewPromociones: RecyclerView
    private lateinit var adapter: PromocionesAdminAdapter
    private lateinit var promocionesAdminManager: PromocionesAdminManager
    private lateinit var fabAdPromociones: FloatingActionButton
    private lateinit var imageAdminMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archivo xml sobre el cual vamos a trabjar*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_promocion)
        /*Asigno objetos xml a las variables anteriormente creadas*/
        recyclerViewPromociones = findViewById(R.id.recyclerViewPromociones)
        fabAdPromociones = findViewById(R.id.fabAddPromociones)
        imageAdminMenu = findViewById(R.id.imageAjustes)
        recyclerViewPromociones.layoutManager = LinearLayoutManager(this)
        promocionesAdminManager = PromocionesAdminManager(this)
        /*Cargo todas las promociones de la base de datos gracias a la llamada a esta funcion*/
        loadPromociones()
        /*Indico que cuando le de al boton este inicie su accion*/
        fabAdPromociones.setOnClickListener {
            val intent = Intent(this, AñadirEditarPromocionAdminActivity::class.java)
            startActivity(intent)
        }
        /*Indico que al pulsar sobre la imagen mostra el menu pop up*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }

    /*Funcion para cargar promociones*/
    private fun loadPromociones() {
        /*Creo una lista en la cual tendre todas las promociones
        * gracias a la llamada al manager de este y el uso de la funcion
        * para obtener todas las promociones*/
        val promocionesList = promocionesAdminManager.getAllPromociones()
        /*Indico que al darle a editar este llame al archivo correspondiente y inicie la actividad y
        * se hace lo mismo con eliminar*/
        adapter = PromocionesAdminAdapter(this, promocionesList,
            onEditClick = { promocion ->
                val intent = Intent(this, AñadirEditarPromocionAdminActivity::class.java)
                intent.putExtra("PROMOCION_ID", promocion.id)
                startActivity(intent)
            },
            onDeleteClick = { promocion ->
                confirmDeletePromociones(promocion)
            }
        )
        recyclerViewPromociones.adapter = adapter
    }

    /*Funcion para eliminar promociones mediante la cual obtengo el id de esta y llamo al manager de promociones
    * y uso la funcion de eliminar*/
    private fun confirmDeletePromociones(promocion: Promociones) {
        val rowsDeleted = promocionesAdminManager.deletePromocion(promocion.id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "${promocion.nombre} eliminada con éxito", Toast.LENGTH_SHORT).show()
            loadPromociones()
        } else {
            Toast.makeText(this, "Error al eliminar ${promocion.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    /*Declaro la función para mostrar el menú de administradores*/
    private fun showAdminPopupMenu(anchorView: View) {
        /*Creo una variable y el popMenu anclado en la vista*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        /*Poso los elementos del menu desde pop_admiin_activity*/
        popupMenu.menuInflater.inflate(R.menu.pop_admin_activity, popupMenu.menu)

        /*Infico que cuando se haga click muestre el menu*/
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
                * me lleve a Peliculas*/
                R.id.admin_menu_peliculas -> {
                    startActivity(Intent(this, PeliculaAdminActivity::class.java))
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a Promociones*/
                R.id.admin_menu_promociones -> {
                    startActivity(Intent(this, PromocionesAdminActivity::class.java))
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a comida*/
                R.id.admin_menu_comida -> {

                    Toast.makeText(this, "Ya estás en la administración de comida", Toast.LENGTH_SHORT).show()
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a Usuarios*/
                R.id.admin_menu_usuarios -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                /*Asigno el objeto en el archivo xml y le indico que en caso de pulsarlo
               * me lleve a Ajustes*/
                R.id.admin_menu_ajustes -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                else -> false
            }
        }
        /*Indico que muestre el menu*/
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        loadPromociones()
    }

    override fun onDestroy() {
        super.onDestroy()
        promocionesAdminManager.close()
    }
}