package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PanelDocumentos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_panel_documentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Boton Ir a Inicio
        val btnIrInicio = findViewById<ImageView>(R.id.img_documentos)
        btnIrInicio.setOnClickListener {
            val intent = Intent(this, PanelAdministrador::class.java)
            startActivity(intent)
        }

        // Boton agrega Documento
        val btnAgregarDocumento: Button = findViewById(R.id.btnAgregarDocumento)
        btnAgregarDocumento.setOnClickListener {
            val intent = Intent(this, AgregarDocumento::class.java)
            startActivity(intent)
        }

        // Boton Editar Documento
        val btnEditarDocumento: Button = findViewById(R.id.btnEditarDocumento)
        btnEditarDocumento.setOnClickListener {
            val intent = Intent(this, EditarDocumento::class.java)
            startActivity(intent)
        }

        // Boton Remover Documento
        val btnRemoverDocumento: Button = findViewById(R.id.btnRemoverDocumento)
        btnRemoverDocumento.setOnClickListener {
            val intent = Intent(this, RemoverDocumento::class.java)
            startActivity(intent)
        }

        // Boton Listado Documentos
        val btnVisibilidadDocumentos: Button = findViewById(R.id.btnVisibilidadDocumento)
        btnVisibilidadDocumentos.setOnClickListener {
            val intent = Intent(this, VerDocumentos::class.java)
            startActivity(intent)
        }

    }
}