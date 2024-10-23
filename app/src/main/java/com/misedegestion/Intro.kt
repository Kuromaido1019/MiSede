package com.misedegestion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage

class Intro : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnIniciarApp: Button
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        videoView = findViewById(R.id.videoView)
        btnIniciarApp = findViewById(R.id.btnIniciarApp)

        // Obtener la referencia del video en Firebase Storage
        val videoRef = storage.reference.child("videos/intro.mp4")

        // Obtener la URL de descarga del video
        videoRef.downloadUrl.addOnSuccessListener { uri ->
            // Reproducir el video en el VideoView
            videoView.setVideoURI(uri)
            videoView.start()  // Empezar el video automáticamente
            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true  // Reproducir el video en bucle
            }
        }.addOnFailureListener { exception ->
            // Manejar errores al obtener la URL
            Toast.makeText(this, "Error al cargar el video: ${exception.message}", Toast.LENGTH_SHORT).show()
        }

        // Configurar el botón para iniciar la aplicación y redirigir a la actividad de Login
        btnIniciarApp.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()  // Cierra la actividad actual para que el usuario no pueda volver a ella
        }
    }
}
