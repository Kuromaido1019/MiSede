package com.misedegestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Llegada : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_llegada)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el nombre del destino del Intent
        val nombreDestino = intent.getStringExtra("DESTINO_NOMBRE") ?: "Destino desconocido"

        // Configurar el TextView con el nombre del destino
        val destinoFinalTextView: TextView = findViewById(R.id.destinoFinalTextView)
        destinoFinalTextView.text = nombreDestino

        //Volver al menu
        val btnVolver: Button = findViewById(R.id.btnVolverInicio)
        btnVolver.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

    }
}