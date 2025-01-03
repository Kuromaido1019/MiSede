package com.misedeg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.misedeg.databinding.ActivityListadoAvisosGeneralesBinding

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
                    val documentId = document.id
                    noticia.id = documentId
                    noticiaList.add(noticia)
                }
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


}
