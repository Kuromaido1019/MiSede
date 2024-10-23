@file:Suppress("DEPRECATION")

package com.misedegestion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseFirestore // Añadido para Firestore

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var googleSignInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Referenciar vistas
        emailField = findViewById(R.id.txtEmail)
        passwordField = findViewById(R.id.txtPass)
        loginButton = findViewById(R.id.button)
        googleSignInButton = findViewById(R.id.btnGoogle)

        // Botón de Registrarme
        val btnRegistrarme: Button = findViewById(R.id.btnRegistrarme)
        btnRegistrarme.setOnClickListener {
            val intent = Intent(this, RegistraUsuario::class.java)
            startActivity(intent)
        }

        // Configurar el botón de login con email y contraseña
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor ingresa el correo y la contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar el botón de login con Google
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("Login", "Attempting login with email: $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            // Verificar tipo de usuario
                            checkUserType(user.uid)
                        } else {
                            Toast.makeText(this, "Por favor verifica tu correo electrónico", Toast.LENGTH_SHORT).show()
                            auth.signOut() // Cierra la sesión del usuario
                        }
                    }
                } else {
                    Log.d("Login", "Login failed", task.exception)
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "Usuario no registrado"
                        is FirebaseAuthInvalidCredentialsException -> "Credenciales no coinciden"
                        else -> "Error desconocido"
                    }
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserType(uid: String) {
        // Consulta Firestore para obtener el tipo de usuario
        val userRef = db.collection("usuarios").document(uid)
        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val tipoUsuario = document.getString("tipoUsuario")
                Log.d("Login", "Tipo de usuario: $tipoUsuario")

                // Redirigir basado en el tipo de usuario
                when (tipoUsuario) {
                    "Administrador" -> {
                        Toast.makeText(this, "Estás ingresando como administrador", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, PanelAdministrador::class.java)
                        startActivity(intent)
                    }
                    "Estudiante" -> {
                        Toast.makeText(this, "Estás ingresando como Estudiante", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java)
                        intent.putExtra("tipoUsuario", tipoUsuario) // Añadir tipo de usuario
                        startActivity(intent)
                    }
                    else -> {
                        Toast.makeText(this, "Tipo de usuario no reconocido", Toast.LENGTH_SHORT).show()
                    }
                }
                finish() // Cerrar la actividad de login
            } else {
                Toast.makeText(this, "Documento de usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.w("Login", "Error al obtener el documento del usuario", exception)
            Toast.makeText(this, "Error al consultar el tipo de usuario", Toast.LENGTH_SHORT).show()
        }
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error de autenticación con Google", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Verificar si el correo está verificado
                        if (user.isEmailVerified) {
                            // Redirigir directamente al Home
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                            finish() // Cerrar la actividad de login
                        } else {
                            // Mostrar mensaje de visitante
                            Toast.makeText(this, "Estás ingresando como visitante", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                            finish() // Cerrar la actividad de login
                        }
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(this, "Error de autenticación: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }


    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
