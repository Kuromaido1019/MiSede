package com.misedegestion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UsuarioActualizado : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuario_actualizado)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Volver al Panel Usuarios
        val btnVolverPanelUsuarios: Button = findViewById(R.id.btnVolverGestionU2)
        btnVolverPanelUsuarios.setOnClickListener {
            val intent = Intent(this, PanelUsuarios::class.java)
            startActivity(intent)
        }

    }
}