package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.misedeg.databinding.ActivityRecuperarCuentaBinding

class RecuperarCuenta : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarCuentaBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecuperarCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajustar el padding para los insets de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configurar el listener para el botón de recuperación de contraseña
        binding.btnRecuperar.setOnClickListener {
            val email = binding.txtEmailRecuperar.text.toString().trim()

            if (email.isEmpty()) {
                binding.txtEmailRecuperar.error = "El correo es obligatorio"
                binding.txtEmailRecuperar.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.txtEmailRecuperar.error = "Introduce un correo válido"
                binding.txtEmailRecuperar.requestFocus()
                return@setOnClickListener
            }

            // Enviar correo de recuperación de contraseña
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show()
                        // Redirigir a la actividad de Login
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al enviar el correo de recuperación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }
}
