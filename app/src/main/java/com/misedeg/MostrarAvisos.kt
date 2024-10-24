package com.misedeg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.misedeg.databinding.ActivityListadoAvisosGeneralesBinding

class MostrarAvisos : AppCompatActivity(), NoticeClickListener {

    private lateinit var binding: ActivityListadoAvisosGeneralesBinding
    private val noticiaList = mutableListOf<Noticia>()
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoAvisosGeneralesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtTituloListado.text = "Todos los Avisos"

        populateNotices()

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = CardAdapter(noticiaList, this@MostrarAvisos, this@MostrarAvisos)
        }
    }

    override fun onClick(noticia: Noticia) {
        val intent = Intent(this, DetalleListadoGeneral::class.java)
        intent.putExtra(NOTICIE_ID_EXTRA, noticia.id)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun populateNotices() {
        db.collection("noticias")
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
