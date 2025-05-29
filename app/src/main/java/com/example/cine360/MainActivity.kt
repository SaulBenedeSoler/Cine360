package com.example.cine360

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cine360.Activity.Login.LoginActivity
import com.example.cine360.Activity.LoginYRegister.RegisterActivity
import com.example.cine360.DataBase.DataBaseHelper

class MainActivity : AppCompatActivity() {

    /*Delcaro una variable para llamar a la base de datos*/

    private lateinit var dbHelper: DataBaseHelper
    /*Funcion que se usa al iniciar la app*/
    override fun onCreate(savedInstanceState: Bundle?) {
        /*Llamo al archivo xml que se trabajara*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*Inicio una instancia con la base de atos*/
        dbHelper = DataBaseHelper(this)
        dbHelper.writableDatabase

        /*Asigno a las variables los objetos xml que tendran asociados*/
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        /*Indico que al darle al boton iniciara la accion correspondiente
        * que se encuentra en el login activity*/
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        /*Indico que al darle al boton iniciara la accion correspondiente
        * que se encuentra en el Register activity*/
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}