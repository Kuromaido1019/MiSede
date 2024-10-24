package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RemoverUsuario : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var spinnerCorreoUsuario: Spinner
    private lateinit var btnRemoveUsuario: Button
    private var emailList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remover_usuario)

        db = Firebase.firestore

        spinnerCorreoUsuario = findViewById(R.id.spnCorreoUsuario)
        btnRemoveUsuario = findViewById(R.id.btnRemoveUsuario)

        cargarEmailsEnSpinner()

        btnRemoveUsuario.setOnClickListener {
            val correo = spinnerCorreoUsuario.selectedItem.toString()

            if (correo.isNotEmpty()) {
                markUserAsInactive(correo)
            } else {
                Toast.makeText(this, "Seleccione un correo válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarEmailsEnSpinner() {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val email = document.getString("email")
                        email?.let {
                            emailList.add(it)
                        }
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emailList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCorreoUsuario.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron usuarios.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar correos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markUserAsInactive(correo: String) {
        db.collection("usuarios").whereEqualTo("email", correo).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentId = documents.documents[0].id
                    val userId = documents.documents[0].getString("UID")

                    if (userId != null) {
                        db.collection("usuarios").document(documentId)
                            .update("estado", "Inactivo")
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario marcado como inactivo en Firestore.", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, UsuarioActualizado::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al marcar usuario como inactivo en Firestore.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "No se encontró el UID del usuario.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado en la base de datos.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al buscar usuario en la base de datos.", Toast.LENGTH_SHORT).show()
            }
    }
}
