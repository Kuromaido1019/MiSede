package com.misedeg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.misedeg.databinding.ActivityListadoAvisosGeneralesBinding

// Define la constante aquí
const val NOTICIE_ID_EXTRA = "noticieExtra"

class ListadoAvisosGenerales : AppCompatActivity(), NoticeClickListener {

    private lateinit var binding: ActivityListadoAvisosGeneralesBinding
    private val noticiaList = mutableListOf<Noticia>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoAvisosGeneralesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titulo = intent.getStringExtra("TITULO")
        binding.txtTituloListado.text = titulo

        populateNotices(titulo)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = CardAdapter(noticiaList, this@ListadoAvisosGenerales, this@ListadoAvisosGenerales)
        }
    }

    override fun onClick(noticia: Noticia) {
        Log.d("CardAdapter", "Clicked on notice: ${noticia.title} with ID: ${noticia.id}")
        val intent = Intent(this, DetalleListadoGeneral::class.java)
        intent.putExtra(NOTICIE_ID_EXTRA, noticia.id) // Pasar el ID como String
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun populateNotices(categoria: String?) {
        db.collection("noticias")
            .whereEqualTo("category", categoria)
            .get()
            .addOnSuccessListener { result ->
                noticiaList.clear()
                for (document in result) {
                    val noticia = document.toObject(Noticia::class.java)
                    val documentId = document.id // Obtener el ID del documento
                    Log.d("ListadoAvisosGenerales", "Loaded notice with ID: $documentId") // Imprimir el ID
                    noticia.id = documentId // Asignar el ID al objeto Noticia
                    noticiaList.add(noticia)
                }
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace() // Manejar el error
            }
    }


}
