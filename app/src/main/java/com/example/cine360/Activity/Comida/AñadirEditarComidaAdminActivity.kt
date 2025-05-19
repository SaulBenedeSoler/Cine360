package com.example.cine360.Activity.Comida

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.DataBase.Manager.ComidaAdminManager
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.R

class AñadirEditarComidaAdminActivity : AppCompatActivity() {

    private lateinit var editTextNameComida: EditText
    private lateinit var editTextDescriptionComida: EditText
    private lateinit var editTextPrecioComida: EditText
    private lateinit var editTextImage: EditText
    private lateinit var buttonSaveComida: Button
    private lateinit var comidaAdminManager: ComidaAdminManager
    private var comidaId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir_comida)

        editTextNameComida = findViewById(R.id.editTextNameComida)
        editTextDescriptionComida = findViewById(R.id.editTextDescriptionComida)
        editTextPrecioComida = findViewById(R.id.editTextPreciocomida)
        editTextImage = findViewById(R.id.editTextImage)
        buttonSaveComida = findViewById(R.id.buttonSaveComida)

        comidaAdminManager = ComidaAdminManager(this)

        comidaId = intent.getIntExtra("COMIDA_ID", -1).takeIf { it != -1 }

        comidaId?.let { loadComidaData(it) }


        buttonSaveComida.setOnClickListener {
            saveComida()
        }


    }

    private fun loadComidaData(comidaId: Int) {
        val comida = comidaAdminManager.getComidaById(comidaId)
        comida?.let {
            editTextNameComida.setText(it.nombre)
            editTextDescriptionComida.setText(it.descripcion)
            editTextPrecioComida.setText(it.precio.toString())
            editTextImage.setText(it.Imagen)
        } ?: run {
            Toast.makeText(this, "Error loading comida data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveComida() {
        val nombre = editTextNameComida.text.toString()
        val descripcion = editTextDescriptionComida.text.toString()
        val precioStr = editTextPrecioComida.text.toString()
        val imagen = editTextImage.text.toString()

        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && precioStr.isNotEmpty() && imagen.isNotEmpty()) {
            val precio = precioStr.toDoubleOrNull() ?: 0.0

            val comida = Comida(
                comidaId ?: 0,
                nombre,
                imagen,
                descripcion,
                precio
            )

            val result = if (comidaId != null) {
                comidaAdminManager.updateComida(comida)
            } else {
                comidaAdminManager.addComida(comida).toInt()
            }

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

    override fun onDestroy() {
        super.onDestroy()
        comidaAdminManager.close()
    }
}
