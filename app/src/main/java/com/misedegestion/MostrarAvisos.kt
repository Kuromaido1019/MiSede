package com.misedegestion

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.misedegestion.databinding.ActivityListadoAvisosGeneralesBinding

class MostrarAvisos : AppCompatActivity(), NoticeClickListener {

    private lateinit var binding: ActivityListadoAvisosGeneralesBinding
    private val noticiaList = mutableListOf<Noticia>()
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoAvisosGeneralesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtTituloListado.text = "Todos los Avisos" // Texto genérico ya que estamos mostrando todos

        // Cargar todas las noticias
        populateNotices()

        // Configurar el RecyclerView con GridLayoutManager
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext, 2)
            adapter = CardAdapter(noticiaList, this@MostrarAvisos, this@MostrarAvisos)
        }
    }

    override fun onClick(noticia: Noticia) {
        Log.d("CardAdapter", "Clicked on notice: ${noticia.title} with ID: ${noticia.id}")
        val intent = Intent(this, DetalleListadoGeneral::class.java)
        intent.putExtra(NOTICIE_ID_EXTRA, noticia.id) // Pasar el ID como String
        startActivity(intent)
    }

    // Cambiar este método para que cargue todos los avisos sin filtro
    @SuppressLint("NotifyDataSetChanged")
    private fun populateNotices() {
        db.collection("noticias")
            .get() // Obtener todos los documentos de la colección
            .addOnSuccessListener { result ->
                noticiaList.clear() // Limpiar la lista antes de agregar nuevos elementos
                for (document in result) {
                    val noticia = document.toObject(Noticia::class.java)
                    val documentId = document.id // Obtener el ID del documento
                    Log.d("MostrarAvisos", "Loaded notice with ID: $documentId") // Imprimir el ID
                    noticia.id = documentId // Asignar el ID al objeto Noticia
                    noticiaList.add(noticia) // Agregar la noticia a la lista
                }
                // Notificar al adaptador que los datos han cambiado
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace() // Manejar el error si ocurre
            }
    }
}
