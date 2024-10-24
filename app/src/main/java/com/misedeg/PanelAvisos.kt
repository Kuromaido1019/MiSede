package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PanelAvisos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_panel_avisos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Boton Ir a Inicio
        val btnIrInicio = findViewById<ImageView>(R.id.img_avisos)
        btnIrInicio.setOnClickListener {
            val intent = Intent(this, PanelAdministrador::class.java)
            startActivity(intent)
        }

        // Boton Agregar Aviso
        val btnAgregarAviso = findViewById<Button>(R.id.btnAgregarAviso)
        btnAgregarAviso.setOnClickListener {
            val intent = Intent(this, AgregarAviso::class.java)
            startActivity(intent)
        }

        // Boton Editar Aviso
        val btnEditarAviso = findViewById<Button>(R.id.btnEditarAviso)
        btnEditarAviso.setOnClickListener {
            val intent = Intent(this, EditarAviso::class.java)
            startActivity(intent)
        }

        // Boton Visibilidad Aviso
        val btnVisibilidadAviso = findViewById<Button>(R.id.btnMostrarAvisos)
        btnVisibilidadAviso.setOnClickListener {
            val intent = Intent(this, MostrarAvisos::class.java)
            startActivity(intent)
        }

        // Boton Remover Aviso
        val btnRemoverAviso = findViewById<Button>(R.id.btnRemoverAviso)
        btnRemoverAviso.setOnClickListener {
            val intent = Intent(this, RemoverAviso::class.java)
            startActivity(intent)
        }


    }
}