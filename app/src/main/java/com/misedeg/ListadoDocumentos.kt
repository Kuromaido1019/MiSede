package com.misedeg

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.misedeg.databinding.ActivityListadoDocumentosBinding

class ListadoDocumentos : AppCompatActivity(), NoticeClickListener {

    private lateinit var binding: ActivityListadoDocumentosBinding
    private val documentoList = mutableListOf<Noticia>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoDocumentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateDocuments()

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = CardAdapter(documentoList, this@ListadoDocumentos, this@ListadoDocumentos)
        }
    }

    override fun onClick(noticia: Noticia) {

        var link = noticia.link // Asegúrate de que el objeto 'Noticia' tenga un atributo 'link'
        if (link.isNotEmpty()) {
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "http://$link"
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        } else {

        }
    }

    private fun populateDocuments() {
        db.collection("documentos")
            .get()
            .addOnSuccessListener { result ->
                documentoList.clear()
                for (document in result) {
                    val noticia = document.toObject(Noticia::class.java)
                    val documentId = document.id
                    noticia.id = documentId
                    documentoList.add(noticia)
                }
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
