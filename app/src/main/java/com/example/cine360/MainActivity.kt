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

    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        dbHelper = DataBaseHelper(this)
        dbHelper.writableDatabase


        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)


        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


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