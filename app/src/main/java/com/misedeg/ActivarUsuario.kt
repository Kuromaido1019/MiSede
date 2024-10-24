package com.misedeg

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ActivarUsuario : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var spinnerCorreoActivar: Spinner
    private lateinit var btnActivarCuenta: Button
    private var emailList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activar_usuario)

        db = Firebase.firestore

        spinnerCorreoActivar = findViewById(R.id.spnActivarUsuario) // Cambia el ID al del Spinner que usarás
        btnActivarCuenta = findViewById(R.id.btnActivarCuenta)

        cargarEmailsEnSpinner()

        btnActivarCuenta.setOnClickListener {
            val email = spinnerCorreoActivar.selectedItem.toString() // Obtener el correo seleccionado

            if (email.isNotEmpty()) {
                activarCuenta(email)
            } else {
                Toast.makeText(this, "Seleccione un correo", Toast.LENGTH_SHORT).show()
            }
        }

        spinnerCorreoActivar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
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
                    spinnerCorreoActivar.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron usuarios.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar correos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun activarCuenta(email: String) {
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val estado = document.getString("estado") ?: "Inactivo"

                        if (estado == "Inactivo") {
                            val userRef = db.collection("usuarios").document(document.id)

                            userRef.update("estado", "Activo")
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Cuenta activada correctamente", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al activar la cuenta", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "El usuario ya está activo", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al buscar usuario", Toast.LENGTH_SHORT).show()
            }
    }
}