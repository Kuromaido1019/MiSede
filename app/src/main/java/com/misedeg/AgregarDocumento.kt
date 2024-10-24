package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Patterns

class AgregarDocumento : AppCompatActivity() {

    private lateinit var txtTituloDocumento: EditText
    private lateinit var txtEnlaceDocumento: EditText
    private lateinit var btnAgregarDocumento: Button
    private lateinit var spnTipoDocAdd: Spinner  // Spinner para categorías

    private val db = FirebaseFirestore.getInstance()  // Instancia de Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Activar diseño "edge to edge"
        setContentView(R.layout.activity_agregar_documento)

        txtTituloDocumento = findViewById(R.id.txtTituloDocumento)
        txtEnlaceDocumento = findViewById(R.id.txtEnlaceDocumento)
        btnAgregarDocumento = findViewById(R.id.btnAgregarDoc)
        spnTipoDocAdd = findViewById(R.id.spnTipoDocAdd)

        val categorias = arrayOf("Institucional", "Estudiantes", "Docentes")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipoDocAdd.adapter = adapter

        btnAgregarDocumento.setOnClickListener {
            agregarDocumento()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun agregarDocumento() {
        val titulo = txtTituloDocumento.text.toString().trim()
        val enlace = txtEnlaceDocumento.text.toString().trim()
        val categoria = spnTipoDocAdd.selectedItem.toString()

        if (titulo.isEmpty() || enlace.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        if (!Patterns.WEB_URL.matcher(enlace).matches()) {
            Toast.makeText(this, "Por favor ingresa un enlace válido.", Toast.LENGTH_LONG).show()
            return
        }

        db.collection("documentos").get().addOnSuccessListener { querySnapshot ->
            val ultimoId = if (querySnapshot.isEmpty) 1 else querySnapshot.size() + 1  // Generar nuevo ID

            val documento = mapOf(
                "title" to titulo,
                "link" to enlace,
                "state" to "active",
                "type" to categoria
            )

            db.collection("documentos").document(ultimoId.toString()).set(documento)
                .addOnSuccessListener {
                    Toast.makeText(this, "Documento agregado exitosamente.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, DocumentoAgregado::class.java)
                    startActivity(intent)  // Redirigir a la actividad de éxito
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al agregar el documento: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}