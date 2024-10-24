package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PanelAdministrador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_panel_administrador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Boton de Documentos
        val btnPanelDocumentos: Button = findViewById(R.id.btnPanelDocumentos)
        btnPanelDocumentos.setOnClickListener {
            val intent = Intent(this, PanelDocumentos::class.java)
            startActivity(intent)
        }

        // Boton Usuarios
        val btnPanelUsuarios: Button = findViewById(R.id.btnPanelUsuarios)
        btnPanelUsuarios.setOnClickListener {
            val intent = Intent(this, PanelUsuarios::class.java)
            startActivity(intent)
        }

        // Boton Avisos
        val btnPanelAvisos: Button = findViewById(R.id.btnPanelAvisos)
        btnPanelAvisos.setOnClickListener {
            val intent = Intent(this, PanelAvisos::class.java)
            startActivity(intent)
        }
    }
}