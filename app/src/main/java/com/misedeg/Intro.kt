package com.misedeg

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class Intro : AppCompatActivity() {

    private lateinit var imageViewGif: ImageView
    private lateinit var btnIniciarApp: Button
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        imageViewGif = findViewById(R.id.videoView)
        btnIniciarApp = findViewById(R.id.btnIniciarApp)

        if (isNetworkAvailable()) {
            loadGifFromFirebase()
        } else {
            loadGifFromDrawable()
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

    private fun loadGifFromFirebase() {
        val gifRef = storage.reference.child("videos/intro.gif")

        gifRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .asGif()
                .load(uri)
                .into(imageViewGif)
        }.addOnFailureListener { exception ->

            Toast.makeText(this, "Error al cargar GIF desde Firebase: ${exception.message}, cargando GIF local.", Toast.LENGTH_SHORT).show()
            loadGifFromDrawable()
        }
    }

    private fun loadGifFromDrawable() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.intro)
            .into(imageViewGif)
    }
}
