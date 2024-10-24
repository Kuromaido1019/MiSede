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

    // Declaración de variables para Firestore y elementos del layout
    private lateinit var db: FirebaseFirestore
    private lateinit var spinnerCorreoActivar: Spinner
    private lateinit var btnActivarCuenta: Button
    private var emailList: ArrayList<String> = ArrayList() // Lista para almacenar los correos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activar_usuario)

        // Inicializar Firestore
        db = Firebase.firestore

        // Referencias a los elementos del layout
        spinnerCorreoActivar = findViewById(R.id.spnActivarUsuario) // Cambia el ID al del Spinner que usarás
        btnActivarCuenta = findViewById(R.id.btnActivarCuenta)

        // Cargar correos electrónicos en el Spinner
        cargarEmailsEnSpinner()

        // Configurar el clic del botón para activar la cuenta
        btnActivarCuenta.setOnClickListener {
            val email = spinnerCorreoActivar.selectedItem.toString() // Obtener el correo seleccionado

            // Verificar que se haya seleccionado un correo
            if (email.isNotEmpty()) {
                activarCuenta(email)  // Llamar a la función de activación
            } else {
                // Mostrar mensaje si no se seleccionó un correo
                Toast.makeText(this, "Seleccione un correo", Toast.LENGTH_SHORT).show()
            }
        }

        // Manejar la selección de un correo en el Spinner
        spinnerCorreoActivar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // No se necesita hacer nada en este caso, pero se puede usar si se necesita alguna lógica extra
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada si no se selecciona ningún email
            }
        }
    }

    // Función para cargar los correos electrónicos en el Spinner
    private fun cargarEmailsEnSpinner() {
        // Consultar Firestore para obtener los emails de todos los usuarios
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val email = document.getString("email")
                        email?.let {
                            emailList.add(it) // Añadir cada email a la lista
                        }
                    }

                    // Crear un adaptador para el Spinner
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

    // Función para activar la cuenta del usuario en Firestore
    private fun activarCuenta(email: String) {
        db.collection("usuarios")  // Referencia a la colección 'usuarios'
            .whereEqualTo("email", email)  // Buscar usuario por correo
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {  // Si se encuentra al menos un usuario
                    for (document in documents) {
                        val estado = document.getString("estado") ?: "Inactivo"  // Obtener estado

                        if (estado == "Inactivo") {
                            val userRef = db.collection("usuarios").document(document.id)

                            // Actualizar el estado del usuario a "Activo"
                            userRef.update("estado", "Activo")
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Cuenta activada correctamente", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al activar la cuenta", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // El usuario ya está activo
                            Toast.makeText(this, "El usuario ya está activo", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // No se encontró el usuario
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                // Error al buscar el usuario en Firestore
                Toast.makeText(this, "Error al buscar usuario", Toast.LENGTH_SHORT).show()
            }
    }
}
