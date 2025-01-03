package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.misedeg.databinding.ActivityRegistraUsuarioBinding

class RegistraUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityRegistraUsuarioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistraUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRecuperarCuenta.setOnClickListener {
            val intent = Intent(this, RecuperarCuenta::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnRegistrarUsuario.setOnClickListener {
            val email = binding.txtEmailRegistro.text.toString().trim()
            val password = binding.txtPassRegistro.text.toString().trim()
            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        if (email.isEmpty()) {
            binding.txtEmailRegistro.error = "El correo es obligatorio"
            binding.txtEmailRegistro.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmailRegistro.error = "Introduce un correo válido"
            binding.txtEmailRegistro.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.txtPassRegistro.error = "La contraseña es obligatoria"
            binding.txtPassRegistro.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.txtPassRegistro.error = "La contraseña debe tener al menos 6 caracteres"
            binding.txtPassRegistro.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = auth.currentUser
                    if (userId != null && user != null) {
                        saveUserToFirestore(userId, email, password, "Estudiante")
                        sendVerificationEmail(user)
                    } else {
                    }
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToFirestore(userId: String, email: String, password: String, userType: String) {
        val user = hashMapOf(
            "email" to email,
            "password" to password,
            "tipoUsuario" to userType,
            "UID" to userId,
            "estado" to "Activo"
        )

        firestore.collection("usuarios")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario guardado en Firestore", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar usuario: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de verificación enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al enviar correo de verificación", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
