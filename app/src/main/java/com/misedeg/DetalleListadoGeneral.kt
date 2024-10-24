package com.misedeg

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.misedeg.databinding.ActivityDetalleListadoGeneralBinding
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class DetalleListadoGeneral : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleListadoGeneralBinding
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleListadoGeneralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noticeID = intent.getStringExtra(NOTICIE_ID_EXTRA) ?: run {
            finish()
            return
        }

        db.collection("noticias").document(noticeID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val noticia = document.toObject(Noticia::class.java)
                    if (noticia != null) {
                        val imageNameWithExtension = "${noticia.cover}.png"

                        val storageRef = storage.reference.child("images/$imageNameWithExtension")

                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            Picasso.get().load(uri).into(binding.cover)
                        }.addOnFailureListener {
                        }
                        binding.title.text = noticia.title
                        binding.author.text = noticia.date
                        binding.description.text = noticia.description
                    } else {
                        finish()
                    }
                } else {
                    finish()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }
}

