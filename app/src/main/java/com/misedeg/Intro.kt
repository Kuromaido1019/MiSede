package com.misedeg

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
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

        if (isNetworkAvailable()) {
            loadVideoFromFirebase()
        } else {
            loadVideoFromRaw()
        }

        btnIniciarApp.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Cargar video desde Firebase Storage
    private fun loadVideoFromFirebase() {
        val videoRef = storage.reference.child("videos/intro.mp4")

        videoRef.downloadUrl.addOnSuccessListener { uri ->
            videoView.setVideoURI(uri)
            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
            }
            videoView.start()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al cargar video desde Firebase: ${exception.message}, cargando video local.", Toast.LENGTH_SHORT).show()
            loadVideoFromRaw()
        }
    }

    // Cargar video desde la carpeta raw
    private fun loadVideoFromRaw() {
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.intro}") // Asegúrate de que el video esté en la carpeta raw
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // El video se reproducirá en bucle
        }
        videoView.start()
    }
}
