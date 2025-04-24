package com.example.cine360

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.cine360.Activity.Semana.SemanaActivity
import com.example.cine360.DataBase.DataBaseHelper

class IndexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)


        val dbHelper = DataBaseHelper(this)
    }

    fun irasemana(view: View?) {
        val intent = Intent(this, SemanaActivity::class.java)
        startActivity(intent)
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}