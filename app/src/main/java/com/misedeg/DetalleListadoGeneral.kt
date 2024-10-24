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
            Log.d("DetalleListadoGeneral", "ID no recibido o inválido")
            finish()
            return
        }
        Log.d("DetalleListadoGeneral", "ID recibido: $noticeID")

        // Consultar Firestore para obtener la noticia
        db.collection("noticias").document(noticeID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val noticia = document.toObject(Noticia::class.java)
                    if (noticia != null) {
                        // Agregar la extensión del archivo .png a la ruta de la imagen
                        val imageNameWithExtension = "${noticia.cover}.png"

                        // Obtener la referencia a la imagen en Firebase Storage
                        val storageRef = storage.reference.child("images/$imageNameWithExtension")

                        // Obtener la URL de descarga de la imagen
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Cargar la imagen desde la URL obtenida
                            Picasso.get().load(uri).into(binding.cover)
                        }.addOnFailureListener {
                            Log.d("DetalleListadoGeneral", "Error al cargar la imagen")
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

