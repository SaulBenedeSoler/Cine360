package com.example.cine360.Activity.Promociones

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.DataBase.Manager.PromocionesAdminManager
import com.example.cine360.DataBase.Tablas.Promociones
import com.example.cine360.R

class AñadirEditarPromocionAdminActivity : AppCompatActivity() {

    private lateinit var editTextNamePromocion: EditText
    private lateinit var editTextDescriptionPromocion: EditText
    private lateinit var editTextPrecioPromocion: EditText
    private lateinit var editTextImage: EditText
    private lateinit var buttonSavePromocion: Button // Corrected the type here
    private lateinit var promocionesAdminManager: PromocionesAdminManager
    private var promocionId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir_promocion)

        editTextNamePromocion = findViewById(R.id.editTextNamePromocion)
        editTextDescriptionPromocion = findViewById(R.id.editTextDescriptionPromocion)
        editTextPrecioPromocion = findViewById(R.id.editTextPrecioPromocion)
        editTextImage = findViewById(R.id.editTextImage)
        buttonSavePromocion = findViewById(R.id.buttonSavePromocion) // Find the Button!

        promocionesAdminManager = PromocionesAdminManager(this)

        promocionId = intent.getIntExtra("PROMOCION_ID", -1).takeIf { it != -1 }

        promocionId?.let { loadPromocionData(it) }


        buttonSavePromocion.setOnClickListener {
            savePromocion()
        }


    }

    private fun loadPromocionData(promocionId: Int) {

        val promocion = promocionesAdminManager.getPromocionById(promocionId)
        promocion?.let {
            editTextNamePromocion.setText(it.nombre)
            editTextDescriptionPromocion.setText(it.descripcion)
            editTextPrecioPromocion.setText(it.precio.toString())
            editTextImage.setText(it.imagen)
        } ?: run {
            Toast.makeText(this, "Error loading promocion data", Toast.LENGTH_SHORT).show()
            finish()
        }


    }

    private fun savePromocion() {
        val nombre = editTextNamePromocion.text.toString()
        val descripcion = editTextDescriptionPromocion.text.toString()
        val precioStr = editTextPrecioPromocion.text.toString() // Get as String first
        val imagen = editTextImage.text.toString()

        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precioStr.isNotEmpty() && imagen.isNotEmpty()) {
            val precio = precioStr.toDoubleOrNull() ?: 0.0  // Convert to Double, handle errors

            val promocion = Promociones(
                promocionId ?: 0,
                nombre,
                imagen,
                descripcion,
                precio
            )

            val result = if (promocionId != null) {
                promocionesAdminManager.updatePromocion(promocion)
            } else {
                promocionesAdminManager.addPromocion(promocion).toInt() // .toInt() for consistency
            }

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

    override fun onDestroy() {
        super.onDestroy()
        promocionesAdminManager.close()
    }
}
