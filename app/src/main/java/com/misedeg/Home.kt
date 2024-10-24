package com.misedeg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

        lblTipoUsuario = findViewById(R.id.lblTipoUsuario)
        db = FirebaseFirestore.getInstance() // Inicializar Firestore

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

        val btnRecorrerST: Button = findViewById(R.id.btnRecorrerST)
        btnRecorrerST.setOnClickListener {
            val intent = Intent(this, Destino::class.java)
            startActivity(intent)
        }

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
            verificarDocumentos()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setTipoUsuarioText() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userRef: DatabaseReference = database.getReference("usuarios").child(userId)

            userRef.child("tipoUsuario").get().addOnSuccessListener { dataSnapshot ->
                val tipoUsuario = dataSnapshot.getValue(String::class.java)
                lblTipoUsuario.text = when (tipoUsuario) {
                    "Administrador" -> "Bienvenido Administrador"
                    "Estudiante" -> "Bienvenido Estudiante"
                    else -> "Bienvenido Usuario"
                }
            }.addOnFailureListener { exception ->
                lblTipoUsuario.text = "Error al obtener el tipo de usuario"
            }
        } else {
            lblTipoUsuario.text = "No estás autenticado"
        }
    }

    private fun verificarAvisos(categoria: String) {
        db.collection("noticias")
            .whereEqualTo("category", categoria)
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
                Toast.makeText(this, "Error al verificar los avisos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verificarDocumentos() {
        db.collection("documentos")
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
                Toast.makeText(this, "Error al verificar los documentos", Toast.LENGTH_SHORT).show()
            }
    }
}

