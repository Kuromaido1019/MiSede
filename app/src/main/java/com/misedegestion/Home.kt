package com.misedegestion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity() {

    private lateinit var lblTipoUsuario: TextView
    private lateinit var db: FirebaseFirestore // Declarar Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicializar los botones y el TextView
        lblTipoUsuario = findViewById(R.id.lblTipoUsuario)
        db = FirebaseFirestore.getInstance() // Inicializar Firestore

        // Obtener el tipo de usuario del Intent
        val tipoUsuario = intent.getStringExtra("tipoUsuario")
        if (tipoUsuario != null) {
            lblTipoUsuario.text = when (tipoUsuario) {
                "Administrador" -> "Bienvenido Administrador"
                "Estudiante" -> "Bienvenido Estudiante"
                else -> "Bienvenido Visitante"
            }
        } else {
            lblTipoUsuario.text = "Bienvenido Visitante"
        }

        // Botones de otras funcionalidades
        val btnRecorrerST: Button = findViewById(R.id.btnRecorrerST)
        btnRecorrerST.setOnClickListener {
            val intent = Intent(this, Destino::class.java)
            startActivity(intent)
        }

        // Configurar los botones de avisos
        val btnAvisosG: Button = findViewById(R.id.btnAvisosG)
        btnAvisosG.setOnClickListener {
            verificarAvisos("Avisos Generales")
        }

        val btnAvisosDAE: Button = findViewById(R.id.btnAvisosDAE)
        btnAvisosDAE.setOnClickListener {
            verificarAvisos("Avisos DAE")
        }

        val btnAvisosCA: Button = findViewById(R.id.btnAvisosCA)
        btnAvisosCA.setOnClickListener {
            verificarAvisos("Centro Aprendizaje")
        }

        val btnDocumentos: Button = findViewById(R.id.btnDocumentos)
        btnDocumentos.setOnClickListener {
            verificarDocumentos() // Verificar documentos antes de abrir la actividad
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setTipoUsuarioText() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Log.d("Home", "User ID: $userId")
            val database = FirebaseDatabase.getInstance()
            val userRef: DatabaseReference = database.getReference("usuarios").child(userId)

            userRef.child("tipoUsuario").get().addOnSuccessListener { dataSnapshot ->
                val tipoUsuario = dataSnapshot.getValue(String::class.java)
                Log.d("Home", "Tipo de Usuario: $tipoUsuario")
                lblTipoUsuario.text = when (tipoUsuario) {
                    "Administrador" -> "Bienvenido Administrador"
                    "Estudiante" -> "Bienvenido Estudiante"
                    else -> "Bienvenido Usuario"
                }
            }.addOnFailureListener { exception ->
                Log.e("Home", "Error retrieving user type", exception)
                lblTipoUsuario.text = "Error al obtener el tipo de usuario"
            }
        } else {
            Log.d("Home", "User not authenticated")
            lblTipoUsuario.text = "No estás autenticado"
        }
    }

    // Función para verificar si hay avisos en la colección
    private fun verificarAvisos(categoria: String) {
        db.collection("noticias") // Cambia esto por el nombre de tu colección de avisos
            .whereEqualTo("category", categoria) // Filtrar por categoría
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val intent = Intent(this, ListadoAvisosGenerales::class.java)
                    intent.putExtra("TITULO", categoria)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No hay elementos para mostrar en $categoria", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Home", "Error retrieving avisos", exception)
                Toast.makeText(this, "Error al verificar los avisos", Toast.LENGTH_SHORT).show()
            }
    }

    // Nueva función para verificar si hay documentos en la colección
    private fun verificarDocumentos() {
        db.collection("documentos") // Cambia esto por el nombre de tu colección de documentos
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val intent = Intent(this, VerDocumentos::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No hay documentos disponibles", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Home", "Error retrieving documentos", exception)
                Toast.makeText(this, "Error al verificar los documentos", Toast.LENGTH_SHORT).show()
            }
    }
}

