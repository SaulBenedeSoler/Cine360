package com.example.cine360.Activity.Promociones

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Comida.ComidaAdminActivity
import com.example.cine360.Activity.LoginYRegister.AdminActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.DataBase.Manager.PromocionesAdminManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R

class AñadirEditarPromocionAdminActivity : AppCompatActivity() {

    /*Declaro diferentes variables que usare para trabajar con diferentes objetos del xml o otros archivos*/
    private lateinit var editTextNamePromocion: EditText
    private lateinit var editTextDescriptionPromocion: EditText
    private lateinit var editTextPrecioPromocion: EditText
    private lateinit var editTextImage: EditText
    private lateinit var buttonSavePromocion: Button
    private lateinit var promocionesAdminManager: PromocionesAdminManager
    private var promocionId: Int? = null
    private lateinit var imageAdminMenu: ImageView

    /*Función que se carga al iniciar le programa*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico el archivo xml sobre el cual trabajara este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir_promocion)

        /*Llamo a las difernetes variables anteriormente delcaradas y les asigno un objeto del arhcivo xml o otro arhcivo*/
        editTextNamePromocion = findViewById(R.id.editTextNamePromocion)
        editTextDescriptionPromocion = findViewById(R.id.editTextDescriptionPromocion)
        editTextPrecioPromocion = findViewById(R.id.editTextPrecioPromocion)
        editTextImage = findViewById(R.id.editTextImage)
        buttonSavePromocion = findViewById(R.id.buttonSavePromocion)
        imageAdminMenu = findViewById(R.id.imageAjustes)

        promocionesAdminManager = PromocionesAdminManager(this)
        /*Indico que coga por id y si esta es 1 lo muestre y si es -1 lo añada*/
        promocionId = intent.getIntExtra("PROMOCION_ID", -1).takeIf { it != -1 }
        /*Llamo a cargar promociones*/
        promocionId?.let { loadPromocionData(it) }
        /*Llamo a guardar promociones*/
        buttonSavePromocion.setOnClickListener {
            savePromocion()
        }
        /*Indico que debe de mostrar el pop up al ser pulsado*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }

    /*Funcion para cargar las promociones*/
    private fun loadPromocionData(promocionId: Int) {
        /*Indico que debe de coger por id todas las promociones*/
        val promocion = promocionesAdminManager.getPromocionById(promocionId)
        promocion?.let {
            /*Llamo  a las variables y les asigno los daots que mostraran*/
            editTextNamePromocion.setText(it.nombre)
            editTextDescriptionPromocion.setText(it.descripcion)
            editTextPrecioPromocion.setText(it.precio.toString())
            editTextImage.setText(it.imagen)
        } ?: run {
            Toast.makeText(this, "Error loading promocion data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*Creo a la funcion guardar promoción*/
    private fun savePromocion() {
        /*Paso todos los datos a string*/
        val nombre = editTextNamePromocion.text.toString()
        val descripcion = editTextDescriptionPromocion.text.toString()
        val precioStr = editTextPrecioPromocion.text.toString()
        val imagen = editTextImage.text.toString()
        /*Compruebo que los datos de estos no esten vacio*/
        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precioStr.isNotEmpty() && imagen.isNotEmpty()) {
            val precio = precioStr.toDoubleOrNull() ?: 0.0
            /*Llamo a la tabla de creación de la base de datos para insertar datos*/
            val promocion = Promociones(
                promocionId ?: 0,
                nombre,
                imagen,
                descripcion,
                precio
            )
            /*Si el id ya esta registrado en la base de datos entonces llamo a guardar promocion y is ese id es neuvo
            * entonces creo una promocion*/
            val result = if (promocionId != null) {
                promocionesAdminManager.updatePromocion(promocion)
            } else {
                promocionesAdminManager.addPromocion(promocion).toInt()
            }
            /*Mensajes que muestro dependiendo del resultado de la operación*/
            if (result > 0) {
                Toast.makeText(this, "Promocion guardada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar la promoción", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    /*Creo función para mostrar le pop up*/
    private fun showAdminPopupMenu(anchorView: View) {
        /*Indico que arhivo se usara en la funcion*/
        val popupMenu = PopupMenu(this, anchorView, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.pop_admin_activity, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                /*Indico que me lleve a Peliculas cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_peliculas -> {
                    startActivity(Intent(this, PeliculaAdminActivity::class.java))
                    true
                }
                /*Indico que me lleve a Promociones cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_promociones -> {
                    Toast.makeText(this, "Ya estás en la administración de promociones", Toast.LENGTH_SHORT).show()
                    true
                }
                /*Indico que me lleve a Comida cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_comida -> {
                    startActivity(Intent(this, ComidaAdminActivity::class.java))
                    true
                }
                /*Indico que me Usuarios a Ajustes cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_usuarios -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                /*Indico que me lleve a Ajustes cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_ajustes -> {
                    startActivity(Intent(this, AjustesUsuarioActivity::class.java))
                    true
                }
                else -> false
            }
        }
        /*Muestro el pop up*/
        popupMenu.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        promocionesAdminManager.close()
    }
}