package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PanelUsuarios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_panel_usuarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIrInicio = findViewById<ImageView>(R.id.img_usuarios)
        btnIrInicio.setOnClickListener {
            val intent = Intent(this, PanelAdministrador::class.java)
            startActivity(intent)
        }

        val btnAgregarUsuario = findViewById<Button>(R.id.btnAgregarUsuario)
        btnAgregarUsuario.setOnClickListener {
            val intent = Intent(this, AgregarUsuario::class.java)
            startActivity(intent)
        }

        val btnPermisosUsuario = findViewById<Button>(R.id.btnPermisosUsuario)
        btnPermisosUsuario.setOnClickListener {
            val intent = Intent(this, PermisosUsuario::class.java)
            startActivity(intent)
        }

        val btnRemoverUsuario = findViewById<Button>(R.id.btnRemoverUsuario)
        btnRemoverUsuario.setOnClickListener {
            val intent = Intent(this, RemoverUsuario::class.java)
            startActivity(intent)
        }

        val btnActivarUsuario = findViewById<Button>(R.id.btnIrActivar)
        btnActivarUsuario.setOnClickListener {
            val intent = Intent(this, ActivarUsuario::class.java)
            startActivity(intent)
        }

        val btnVerDatosUsuario = findViewById<Button>(R.id.btnVerDatosUsuario)
        btnVerDatosUsuario.setOnClickListener {
            val intent = Intent(this, VerDatosUsuario::class.java)
            startActivity(intent)
        }
    }
}