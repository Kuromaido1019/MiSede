package com.misedeg

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VerDatosUsuario : AppCompatActivity() {

    private lateinit var txtEmailBuscar: EditText
    private lateinit var lblEmailBuscar: TextView
    private lateinit var lblEstadoBuscar: TextView
    private lateinit var lblPermisoBuscar: TextView
    private lateinit var btnBuscarDatos: Button
    private lateinit var spinnerEmailBuscar: Spinner

    private val db: FirebaseFirestore = Firebase.firestore
    private var emailList: ArrayList<String> = ArrayList() // Lista para almacenar los correos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_datos_usuario)

        lblEmailBuscar = findViewById(R.id.lblEmailBuscar)
        lblEstadoBuscar = findViewById(R.id.lblEstadoBuscar)
        lblPermisoBuscar = findViewById(R.id.lblPermisoBuscar)
        btnBuscarDatos = findViewById(R.id.btnBuscarDatos)
        spinnerEmailBuscar = findViewById(R.id.spinnerEmailBuscar)

        cargarEmailsEnSpinner()

        btnBuscarDatos.setOnClickListener {
            val email = txtEmailBuscar.text.toString().trim()

            if (email.isNotEmpty()) {
                buscarUsuarioPorEmail(email)
            } else {
                Toast.makeText(this, "Por favor ingresa un correo", Toast.LENGTH_SHORT).show()
            }
        }

        spinnerEmailBuscar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedEmail = emailList[position]
                buscarUsuarioPorEmail(selectedEmail)
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
                    spinnerEmailBuscar.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron usuarios.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar correos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarUsuarioPorEmail(email: String) {
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val emailUsuario = document.getString("email")
                        val estado = document.getString("estado")
                        val tipoUsuario = document.getString("tipoUsuario")

                        lblEmailBuscar.text = emailUsuario
                        lblEstadoBuscar.text = estado ?: "No especificado"
                        lblPermisoBuscar.text = tipoUsuario ?: "No especificado"
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al buscar datos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
