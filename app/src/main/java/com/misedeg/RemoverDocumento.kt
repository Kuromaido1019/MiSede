package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RemoverDocumento : AppCompatActivity() {

    private lateinit var spinnerDocumentosRemover: Spinner
    private lateinit var btnRemoverDoc: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remover_documento)

        spinnerDocumentosRemover = findViewById(R.id.spinnerDocumentosRemover)
        btnRemoverDoc = findViewById(R.id.btnRemoverDoc)

        cargarDocumentos()

        btnRemoverDoc.setOnClickListener {
            eliminarDocumento()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarDocumentos() {
        db.collection("documentos").get().addOnSuccessListener { querySnapshot ->
            val documentos = querySnapshot.documents.map { it.getString("title") ?: "Sin título" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, documentos)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDocumentosRemover.adapter = adapter
        }
    }

    private fun eliminarDocumento() {
        val tituloSeleccionado = spinnerDocumentosRemover.selectedItem.toString()

        if (tituloSeleccionado.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona un documento para eliminar.", Toast.LENGTH_LONG).show()
            return
        }

        db.collection("documentos")
            .whereEqualTo("title", tituloSeleccionado)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(this, "No se encontró el documento seleccionado.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                val documento = querySnapshot.documents.first()
                val id = documento.id
                val nombreImagen = documento.getString("cover") ?: ""

                db.collection("documentos").document(id).delete()
                    .addOnSuccessListener {
                        val storageRef = FirebaseStorage.getInstance().reference.child("images/$nombreImagen.png")
                        storageRef.delete().addOnSuccessListener {
                            Toast.makeText(this, "Documento eliminado exitosamente.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, DocumentoRemovido::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Error al eliminar la imagen: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al eliminar el documento: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al consultar el documento: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
