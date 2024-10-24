package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PermisosUsuario : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var spinnerEmailUser: Spinner
    private lateinit var btnDarPermiso: Button
    private lateinit var btnQuitarPermiso: Button
    private var emailList: ArrayList<String> = ArrayList() // Lista para almacenar los correos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisos_usuario)

        db = Firebase.firestore

        spinnerEmailUser = findViewById(R.id.spnPermisoUsuario)
        btnDarPermiso = findViewById(R.id.btnDarPermiso)
        btnQuitarPermiso = findViewById(R.id.btnQuitarPermiso)

        cargarEmailsEnSpinner()

        btnDarPermiso.setOnClickListener {
            val email = spinnerEmailUser.selectedItem.toString()

            if (email.isNotEmpty()) {
                actualizarPermisosUsuario(email, "Administrador")
            } else {
                Toast.makeText(this, "Seleccione un correo válido", Toast.LENGTH_SHORT).show()
            }
        }

        btnQuitarPermiso.setOnClickListener {
            val email = spinnerEmailUser.selectedItem.toString()

            if (email.isNotEmpty()) {
                actualizarPermisosUsuario(email, "Estudiante")
            } else {
                Toast.makeText(this, "Seleccione un correo válido", Toast.LENGTH_SHORT).show()
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
                    spinnerEmailUser.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron usuarios.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar correos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarPermisosUsuario(email: String, nuevoTipoUsuario: String) {
        val userRef = db.collection("usuarios").whereEqualTo("email", email)

        userRef.get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val documentId = documents.documents[0].id
                val updates = hashMapOf<String, Any>(
                    "tipoUsuario" to nuevoTipoUsuario
                )
                db.collection("usuarios").document(documentId)
                    .update(updates)
                    .addOnSuccessListener {
                        val message = if (nuevoTipoUsuario == "Administrador") {
                            "Permisos de administrador otorgados."
                        } else {
                            "Permisos de administrador retirados."
                        }
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, UsuarioActualizado::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar los permisos.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al buscar el usuario.", Toast.LENGTH_SHORT).show()
        }
    }
}
