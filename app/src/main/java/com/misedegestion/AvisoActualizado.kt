package com.misedegestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AvisoActualizado : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aviso_actualizado)

        // Ajustar insets para la UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el botón para volver al panel de avisos
        val btnVolver = findViewById<Button>(R.id.btnVolverAvisosE)
        btnVolver.setOnClickListener {
            val intent = Intent(this, PanelAvisos::class.java)
            startActivity(intent)
            finish() // Finalizar esta actividad para evitar que el usuario regrese a ella al presionar "Atrás"
        }
    }
}