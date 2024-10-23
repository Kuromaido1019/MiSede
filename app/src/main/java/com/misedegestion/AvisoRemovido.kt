package com.misedegestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AvisoRemovido : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Configura la actividad para usar el espacio de borde a borde
        setContentView(R.layout.activity_aviso_removido)

        // Ajustar los insets del sistema para evitar que la interfaz se vea cortada
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el botón para volver al panel de avisos
        val btnVolver = findViewById<Button>(R.id.btnAvisoRemovidoR)
        btnVolver.setOnClickListener {
            val intent = Intent(this, PanelAvisos::class.java)
            startActivity(intent)
            finish() // Finaliza la actividad actual para evitar que el usuario regrese a ella con el botón "Atrás"
        }
    }
}
