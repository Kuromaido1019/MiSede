package com.misedegestion

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.misedegestion.databinding.ActivityListadoDocumentosBinding

class ListadoDocumentos : AppCompatActivity(), NoticeClickListener {

    private lateinit var binding: ActivityListadoDocumentosBinding
    private val documentoList = mutableListOf<Noticia>() // Cambia 'Noticia' a 'Documento' si es necesario
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoDocumentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Llama a la función para cargar los documentos
        populateDocuments()

        // Configura el RecyclerView con un GridLayoutManager
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = CardAdapter(documentoList, this@ListadoDocumentos, this@ListadoDocumentos)
        }
    }

    // Manejar clic en el documento
    override fun onClick(noticia: Noticia) {
        Log.d("CardAdapter", "Clicked on document: ${noticia.title} with ID: ${noticia.id}")

        // Obtener el enlace del documento y abrirlo en el navegador
        var link = noticia.link // Asegúrate de que el objeto 'Noticia' tenga un atributo 'link'
        if (link.isNotEmpty()) {
            // Verificar si el enlace tiene el prefijo http o https, si no, agregarlo
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "http://$link"
            }

            // Crear un Intent para abrir el navegador con el enlace
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        } else {
            Log.e("ListadoDocumentos", "No link found for document: ${noticia.title}")
        }
    }

    // Función para cargar todos los documentos de Firestore
    @SuppressLint("NotifyDataSetChanged")
    private fun populateDocuments() {
        db.collection("documentos")
            .get()
            .addOnSuccessListener { result ->
                documentoList.clear()
                for (document in result) {
                    val noticia = document.toObject(Noticia::class.java)
                    val documentId = document.id // Obtener el ID del documento
                    Log.d("ListadoDocumentos", "Loaded document with ID: $documentId")
                    noticia.id = documentId // Asignar el ID al objeto Noticia (o Documento)
                    documentoList.add(noticia)
                }
                // Notificar al adaptador que los datos han cambiado
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace() // Manejar el error
            }
    }
}
