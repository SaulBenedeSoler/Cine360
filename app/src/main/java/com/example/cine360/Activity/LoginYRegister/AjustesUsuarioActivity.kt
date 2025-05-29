package com.example.cine360.Activity.LoginYRegister

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AjustesUsuarioActivity : AppCompatActivity() {

    /*Declaramos las variables que usaremos para asignarles objetos xml o otros archivos*/
    private lateinit var editTextNombre: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var btnGuardarCambios: Button
    private lateinit var btnEliminarCuenta: Button
    private lateinit var dbHelper: DataBaseHelper
    private var userId: Int = -1
    private lateinit var userEmail: String

    /*Funcion que se ejecuta al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Indicamos el xml sobre el que va a trabjar el archivo*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes_usuario)

        /*Llamamos a las variables anteriormente declaradas y le asignamos objetos xml
        * Abrimos una instancia con la base de datos*/
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextCorreo = findViewById(R.id.editTextCorreo)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)
        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta)
        dbHelper = DataBaseHelper(this)

        /*COmprobamos todos los datos de ese usuario y los mostramos mediante la obtencion de su id*/
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("usuario_id", -1)
        val userName = sharedPreferences.getString("usuario_nombre", "") ?: ""
        userEmail = sharedPreferences.getString("usuario_email", "") ?: ""
        val userContrasena = ""

        editTextNombre.setText(userName)
        editTextCorreo.setText(userEmail)
        editTextContrasena.setText(userContrasena)

        /*Al pulsar sobre convertimos todo a string y guardamos la nueva informacion*/
        btnGuardarCambios.setOnClickListener {
            val nuevoNombre = editTextNombre.text.toString().trim()
            val nuevoCorreo = editTextCorreo.text.toString().trim()
            val nuevaContrasena = editTextContrasena.text.toString().trim()

            if (nuevoNombre.isEmpty() || nuevoCorreo.isEmpty()) {
                Toast.makeText(this, "Nombre y correo son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /*En caso de introducir un dato erroeno se muestra al usuario el error*/
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()) {
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            /*Llamamos a la funcion para actualizar los datos en la base de datos*/
            actualizarDatosUsuario(nuevoNombre, nuevoCorreo, nuevaContrasena)
        }

        /*Llamos a la funcion de eliminar cuenta al pulsar sobre el boton*/
        btnEliminarCuenta.setOnClickListener {
            eliminarCuenta()
        }
    }

    /*Funcion para actualizar los datos del usuario en la base de datos*/
    private fun actualizarDatosUsuario(nuevoNombre: String, nuevoCorreo: String, nuevaContrasena: String) {

        /*Usamos corrutinas para obtener los datos del usuario y declaramos una instancia en la base de datos*/
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.writableDatabase
                val values = android.content.ContentValues().apply {
                    put(DataBaseHelper.COLUMN_NOMBRE, nuevoNombre)
                    put(DataBaseHelper.COLUMN_EMAIL, nuevoCorreo)
                    if (nuevaContrasena.isNotEmpty()) {
                        put(DataBaseHelper.COLUMN_PASSWORD, nuevaContrasena)
                    }
                }
                /*Indicamos los valores que debne tener*/
                val whereClause = "${DataBaseHelper.COLUMN_ID} = ?"
                val whereArgs = arrayOf(userId.toString())
                val rowsUpdated = db.update(DataBaseHelper.TABLE_USERS, values, whereClause, whereArgs)

                /*En caso de que todo sea correcto lo indicamos mediante un mensaje, aplicamos los cambios y cerramos la instancia con la base de datos
                *                 En caso de tener un error lo mostramos*/
                withContext(Dispatchers.Main) {
                    if (rowsUpdated > 0) {
                        Toast.makeText(this@AjustesUsuarioActivity, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()

                        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("usuario_nombre", nuevoNombre)
                        editor.putString("usuario_email", nuevoCorreo)
                        editor.apply()
                        finish()
                    } else {
                        Toast.makeText(this@AjustesUsuarioActivity, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
                    }
                }
                /*Cerramos conexion con la base de datos*/
                db.close()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AjustesUsuarioActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*Funcion para eliminar la cuenta de un usuario*/
    private fun eliminarCuenta() {
        /*Mediante corrutinas obtenemos los datos y creamos una instancia en la base de datos*/
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.writableDatabase
                val whereClause = "${DataBaseHelper.COLUMN_ID} = ?"
                val whereArgs = arrayOf(userId.toString())
                val rowsDeleted = db.delete(DataBaseHelper.TABLE_USERS, whereClause, whereArgs)
                /*Si tood a salido correcto se meustra que se a eliminado la cuenta*/
                withContext(Dispatchers.Main) {
                    if (rowsDeleted > 0) {
                        Toast.makeText(this@AjustesUsuarioActivity, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                        /*Mediante preferencia obtenemos los datos a eliminar, limpiamos la base de datos para
                        * que el usuario deje de estar almacenado en esta, aplicamos estos cambios y finalizamos la accion*/
                        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.apply()
                        val intent = Intent(this@AjustesUsuarioActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@AjustesUsuarioActivity, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                    }
                }
                /*Cerramos conexion con la base de datos*/
                db.close()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AjustesUsuarioActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}