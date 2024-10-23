package com.misedegestion

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

class RemoverAviso : AppCompatActivity() {

    private lateinit var spinnerAvisosRemover: Spinner
    private lateinit var btnRemoverAvisoR: Button
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var listaTitulos = mutableListOf<String>()
    private var listaIds = mutableListOf<String>()  // Para almacenar los IDs de los documentos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remover_aviso)

        spinnerAvisosRemover = findViewById(R.id.spinnerAvisosRemover)
        btnRemoverAvisoR = findViewById(R.id.btnRemoverAvisoR)

        cargarAvisos()

        btnRemoverAvisoR.setOnClickListener {
            removerAviso()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Método para cargar los avisos en el Spinner
    private fun cargarAvisos() {
        db.collection("noticias").get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                for (document in querySnapshot) {
                    listaTitulos.add(document.getString("title") ?: "")
                    listaIds.add(document.id)  // Guardamos el ID del documento
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaTitulos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerAvisosRemover.adapter = adapter
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al cargar avisos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Método para remover el aviso seleccionado
    private fun removerAviso() {
        val posicionSeleccionada = spinnerAvisosRemover.selectedItemPosition
        if (posicionSeleccionada != -1) {
            val avisoId = listaIds[posicionSeleccionada]

            // Eliminar el documento de Firestore
            db.collection("noticias").document(avisoId).get().addOnSuccessListener { documentSnapshot ->
                val nombreImagen = documentSnapshot.getString("cover") ?: ""

                // Eliminar el aviso de Firestore
                db.collection("noticias").document(avisoId).delete().addOnSuccessListener {

                    // Eliminar la imagen correspondiente de Firebase Storage
                    val storageRef = storage.reference.child("images/$nombreImagen.png")
                    storageRef.delete().addOnSuccessListener {
                        Toast.makeText(this, "Aviso y imagen eliminados exitosamente.", Toast.LENGTH_LONG).show()

                        // Redirigir a la actividad AvisoRemovido
                        val intent = Intent(this, AvisoRemovido::class.java)
                        startActivity(intent)
                        finish()

                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Error al eliminar la imagen: ${e.message}", Toast.LENGTH_LONG).show()
                    }

                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al eliminar el aviso: ${e.message}", Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener datos del aviso: ${e.message}", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "Seleccione un aviso para remover.", Toast.LENGTH_LONG).show()
        }
    }
}
