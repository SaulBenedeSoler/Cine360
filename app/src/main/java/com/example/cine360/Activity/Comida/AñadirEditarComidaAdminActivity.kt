package com.example.cine360.Activity.Comida

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.DataBase.Manager.ComidaAdminManager
import com.example.cine360.DataBase.Tablas.Comida
import com.example.cine360.R

class AñadirEditarComidaAdminActivity : AppCompatActivity() {

    private lateinit var  editTextNameComida: EditText
    private lateinit var  editTextDescriptionComida: EditText
    private lateinit var editTextPrecioComida: EditText
    private lateinit var editTextImage: EditText
    private lateinit var buttonSaveComida: EditText
    private lateinit var  comidaAdminManager: ComidaAdminManager
    private var comidaId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_anadir_promocion)

        editTextNameComida = findViewById(R.id.editTextNamePromocion)
        editTextDescriptionComida = findViewById(R.id.editTextDescriptionPromocion)
        editTextPrecioComida = findViewById(R.id.editTextPrecioPromocion)
        editTextImage = findViewById(R.id.editTextImage)
        buttonSaveComida = findViewById(R.id.buttonSavePromocion)

        comidaAdminManager = ComidaAdminManager(this)

        comidaId = intent.getIntExtra("COMIDA_ID", -1).takeIf { it != -1 }

        comidaId?.let { loadComidaData(it) }


        buttonSaveComida.setOnClickListener {
            saveComida()
        }


    }

    private fun loadComidaData(comidaId: Int){

        val comida = comidaAdminManager.getComidaById(comidaId)
        comida?.let {
            editTextNameComida.setText(it.nombre)
            editTextDescriptionComida.setText(it.descripcion)
            editTextPrecioComida.setText(it.precio.toString())
            editTextImage.setText(it.Imagen)
        }?: run{
            Toast.makeText(this, "Error loading comida data", Toast.LENGTH_SHORT).show()
            finish()
        }


    }

    private fun saveComida(){
        val nombre = editTextNameComida.text.toString()
        val descripcion = editTextDescriptionComida.text.toString()
        val precio = editTextPrecioComida.text.toString()
        val imagen = editTextImage.text.toString()

        if(nombre.isNotEmpty() && descripcion.isNotEmpty() && precio.isNotEmpty() && imagen.isNotEmpty()){
            val precio = precio.toDouble()
            val comida = Comida(
                comidaId?: 0,
                nombre,
                imagen ,
                descripcion,
                precio
            )

            val result = if(comidaId != null){
                comidaAdminManager.updateComida(comida)
            }else{
                comidaAdminManager.addComida(comida)
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        comidaAdminManager.close()
    }


}

