package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class AgregarUsuario : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var radioGroupTipoUsuario: RadioGroup
    private lateinit var btnAgregarUser: Button

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_usuario)

        // Evitar que la interfaz se vea cortada por los bordes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referencias a los componentes
        radioGroupTipoUsuario = findViewById(R.id.radioGroupTipoUsuario)
        btnAgregarUser = findViewById(R.id.btnAgregarUser)
        editTextEmail = findViewById(R.id.editTextTextEmailAddress)
        editTextPassword = findViewById(R.id.editTextText3)

        // Configurar el botón para agregar usuario
        btnAgregarUser.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Validar campos
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Por favor, ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Por favor, ingrese una contraseña de al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener el tipo de usuario seleccionado
            val selectedRadioButtonId = radioGroupTipoUsuario.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Seleccione un tipo de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val tipoUsuario = selectedRadioButton.text.toString()

            // Registrar al usuario y guardar en Firestore
            registerUser(email, password, tipoUsuario)
        }
    }

    // Método para registrar al usuario y guardar en Firestore
    private fun registerUser(email: String, password: String, tipoUsuario: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Crear mapa de usuario para Firestore
                        val userMap = hashMapOf(
                            "email" to email,
                            "tipoUsuario" to tipoUsuario,
                            "uid" to userId,
                            "estado" to "Activo"
                        )

                        // Guardar el usuario en Firestore bajo su UID
                        firestore.collection("usuarios").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario agregado exitosamente", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, UsuarioAgregado::class.java)
                                startActivity(intent)
                                finish() // Finalizar actividad actual
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar el usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Manejar error de registro
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Validación básica de correo electrónico
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        )
        return emailPattern.matcher(email).matches()
    }
}
