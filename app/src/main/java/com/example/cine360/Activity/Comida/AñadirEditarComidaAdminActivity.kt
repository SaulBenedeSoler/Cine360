package com.example.cine360.Activity.Comida

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.LoginYRegister.AdminActivity
import com.example.cine360.Activity.LoginYRegister.AjustesUsuarioActivity
import com.example.cine360.Activity.Pelicula.PeliculaAdminActivity
import com.example.cine360.Activity.Promociones.PromocionesAdminActivity
import com.example.cine360.DataBase.Manager.ComidaAdminManager
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.R

class AñadirEditarComidaAdminActivity : AppCompatActivity() {

    /*Declaro las variables que luego asignare a diferentes objetos de xml o usare para llamar a archivos*/
    private lateinit var editTextNameComida: EditText
    private lateinit var editTextDescriptionComida: EditText
    private lateinit var editTextPrecioComida: EditText
    private lateinit var editTextImage: EditText
    private lateinit var buttonSaveComida: Button
    private lateinit var comidaAdminManager: ComidaAdminManager
    private var comidaId: Int? = null
    private lateinit var imageAdminMenu: ImageView

    /*Función que se usa cuando se inicia por primera vez el programa*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indico  cual sera el archivo xml asignado a este archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir_promocion)

        /*Llamo a las variables anteriormente declaradas y les asigno sus objetos xml y sus archivos*/
        editTextNameComida = findViewById(R.id.editTextNamePromocion)
        editTextDescriptionComida = findViewById(R.id.editTextDescriptionPromocion)
        editTextPrecioComida = findViewById(R.id.editTextPrecioPromocion)
        editTextImage = findViewById(R.id.editTextImage)
        buttonSaveComida = findViewById(R.id.buttonSavePromocion)
        imageAdminMenu = findViewById(R.id.imageAjustes)

        comidaAdminManager = ComidaAdminManager(this)
        /*Cogo el id de la comida en caso de no existir se da a enteder que se esta creando uno nuvo*/
        comidaId = intent.getIntExtra("COMIDA_ID", -1).takeIf { it != -1 }
        /*Indico que debe usar los datos de la comida en caso de ser existente y no nulos*/
        comidaId?.let { loadComidaData(it) }
        /*Indico que cuando se pulse el botón llame a la función de guardar comida*/
        buttonSaveComida.setOnClickListener {
            saveComida()
        }
        /*Indico que cuando le de a la imagen me meustre el popup*/
        imageAdminMenu.setOnClickListener {
            showAdminPopupMenu(it)
        }
    }

    /*Función para cargar todos los datos de la comida guardados en la base de datos*/
    private fun loadComidaData(comidaId: Int) {
        /*Declaro una variable y le indico que recibira una lista
        * de todas las comidas almacenadas en la base de datos
        * y buscadas por id*/
        val comida = comidaAdminManager.getComidaById(comidaId)
        /*Si las encuentra entonces me mostrara los datos usando las variables asignadas y en caso de no encontrarli
        * me mostrara un error*/
        comida?.let {
            editTextNameComida.setText(it.nombre)
            editTextDescriptionComida.setText(it.descripcion)
            editTextPrecioComida.setText(it.precio.toString())
            editTextImage.setText(it.Imagen)
        } ?: run {
            Toast.makeText(this, "Error al cargar los datos de la comida", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*Función para guardar comida*/
    private fun saveComida() {
        /*Le asigno a las variables anteriormente creadas sus respectivos datos y los convierto a string*/
        val nombre = editTextNameComida.text.toString().trim()
        val descripcion = editTextDescriptionComida.text.toString().trim()
        val precioStr = editTextPrecioComida.text.toString().trim()
        val imagen = editTextImage.text.toString().trim()

        /*Compruebo que todos los datos contengan información y no esten vacios*/
        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precioStr.isNotEmpty() && imagen.isNotEmpty()) {
            /*Convierto a Double el precio, en caso de ser nulo indico un mensaje de error*/
            val precio = precioStr.toDoubleOrNull()
            if (precio == null) {
                Toast.makeText(this, "Por favor, introduce un precio válido", Toast.LENGTH_SHORT).show()
                return
            }
            /*Creo una variable comida y llamo a la tabla de la base de datos*/
            val comida = Comida(
                comidaId ?: 0,
                nombre,
                imagen,
                descripcion,
                precio
            )

            /*Indico con el if si se debe añadir una nueva comida a la base de datos o se debe de actualizar*/
            val result = if (comidaId != null) {
                comidaAdminManager.updateComida(comida)
            } else {
                comidaAdminManager.addComida(comida).toInt()
            }

            /*Si el resultado es correcto indico que se a completado con existo, en caso de error
            * o no rellenar cmapos se muestra un mensaje*/
            if (result > 0) {
                Toast.makeText(this, "Comida guardada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar la comida", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    /*Función para mostrar el menu */
    private fun showAdminPopupMenu(anchorView: View) {
        /*Indico que el menu estara en el archivo pop_admin_Activity*/
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
                    startActivity(Intent(this, PromocionesAdminActivity::class.java))
                    true
                }
                /*Indico que me lleve a Comida cuando pulse sobre el objeto relacionado el id*/
                R.id.admin_menu_comida -> {
                    startActivity(Intent(this, ComidaAdminActivity::class.java))
                    true
                }
                /*Indico que me lleve a Usuarios cuando pulse sobre el objeto relacionado el id*/
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
        /*Muestro el menu*/
        popupMenu.show()
    }

    /*Funcion usada para eliminar y asi que el usuario no pueda ver.*/
    override fun onDestroy() {
        super.onDestroy()
        comidaAdminManager.close()
    }
}
