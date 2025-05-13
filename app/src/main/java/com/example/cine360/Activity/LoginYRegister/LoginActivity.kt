package com.example.cine360.Activity.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.LoginYRegister.AdminActivity
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.IndexActivity
import com.example.cine360.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.etUsername)
        editTextPassword = findViewById(R.id.etPassword)
        buttonLogin = findViewById(R.id.btnLogin)
        dbHelper = DataBaseHelper(this)


        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = dbHelper.readableDatabase
                val cursor = db.query(
                    DataBaseHelper.TABLE_USERS,
                    arrayOf(
                        DataBaseHelper.COLUMN_ID,
                        DataBaseHelper.COLUMN_NOMBRE,
                        DataBaseHelper.COLUMN_EMAIL,
                        DataBaseHelper.COLUMN_IS_ADMIN
                    ),
                    "${DataBaseHelper.COLUMN_EMAIL} = ? AND ${DataBaseHelper.COLUMN_PASSWORD} = ?",
                    arrayOf(email, password),
                    null, null, null
                )

                withContext(Dispatchers.Main) {
                    if (cursor != null && cursor.moveToFirst()) {
                        val idColumnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID)
                        val nombreColumnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_NOMBRE)
                        val isAdminColumnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_IS_ADMIN)

                        if (idColumnIndex != -1 && nombreColumnIndex != -1 && isAdminColumnIndex != -1) {
                            val userId = cursor.getInt(idColumnIndex)
                            val userName = cursor.getString(nombreColumnIndex)
                            val isAdmin = cursor.getInt(isAdminColumnIndex) == 1
                            guardarDatosUsuario(userId, userName, email, isAdmin)

                            Log.d(
                                "LoginActivity",
                                "Inicio de sesión exitoso para el usuario ID: $userId, Nombre: $userName, isAdmin: $isAdmin"
                            )


                            val intent = if (isAdmin) {
                                Intent(this@LoginActivity, AdminActivity::class.java)
                            } else {
                                Intent(this@LoginActivity, IndexActivity::class.java)
                            }
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Error al obtener los datos del usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(
                                "LoginActivity",
                                "Error en los índices de las columnas: id=$idColumnIndex, nombre=$nombreColumnIndex, isAdmin=$isAdminColumnIndex"
                            )
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Email o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    cursor?.close()
                }
                db.close()
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error en el inicio de sesión: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error al iniciar sesión: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun guardarDatosUsuario(userId: Int, userName: String, userEmail: String, isAdmin: Boolean) {
        try {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("usuario_id", userId)
            editor.putString("usuario_nombre", userName)
            editor.putString("usuario_email", userEmail)
            editor.putBoolean("usuario_is_admin", isAdmin)
            val result = editor.commit()
            if (result) {
                Log.d("LoginActivity", "Datos del usuario guardados correctamente en SharedPreferences")
                val storedId = sharedPreferences.getInt("usuario_id", -1)
                val storedIsAdmin = sharedPreferences.getBoolean("usuario_is_admin", false)
                Log.d("LoginActivity", "ID almacenado en SharedPreferences: $storedId, isAdmin: $storedIsAdmin")
            } else {
                Log.e("LoginActivity", "Error al guardar los datos del usuario en SharedPreferences")
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error al guardar los datos del usuario: ${e.message}")
        }
    }

    companion object {
        fun cerrarSesion(context: Context) {
            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
