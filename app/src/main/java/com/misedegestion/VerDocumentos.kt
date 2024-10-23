package com.misedegestion

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VerDocumentos : AppCompatActivity() {

    private lateinit var detailsTextInstitucional: TextView
    private lateinit var layoutInstitucional: LinearLayout
    private lateinit var expandInstitucional: CardView
    private lateinit var documentsRecyclerViewInstitucional: RecyclerView

    private lateinit var detailsTextEstudiantes: TextView
    private lateinit var layoutEstudiantes: LinearLayout
    private lateinit var expandEstudiantes: CardView
    private lateinit var documentsRecyclerViewEstudiantes: RecyclerView

    private lateinit var detailsTextDocentes: TextView
    private lateinit var layoutDocentes: LinearLayout
    private lateinit var expandDocentes: CardView
    private lateinit var documentsRecyclerViewDocentes: RecyclerView

    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_documentos)

        // Inicialización para Institucional
        detailsTextInstitucional = findViewById(R.id.descriptionInstitucional)
        layoutInstitucional = findViewById(R.id.layoutsInstitucional)
        expandInstitucional = findViewById(R.id.cardViewInstitucional)
        documentsRecyclerViewInstitucional = findViewById(R.id.documentsRecyclerViewInstitucional)

        // Inicialización para Estudiantes
        detailsTextEstudiantes = findViewById(R.id.descriptionEstudiantes)
        layoutEstudiantes = findViewById(R.id.layoutsEstudiantes)
        expandEstudiantes = findViewById(R.id.cardViewEstudiantes)
        documentsRecyclerViewEstudiantes = findViewById(R.id.documentsRecyclerViewEstudiantes)

        // Inicialización para Docentes
        detailsTextDocentes = findViewById(R.id.descriptionDocentes)
        layoutDocentes = findViewById(R.id.layoutsDocentes)
        expandDocentes = findViewById(R.id.cardViewDocentes)
        documentsRecyclerViewDocentes = findViewById(R.id.documentsRecyclerViewDocentes)

        // Configuración de transiciones
        layoutInstitucional.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        layoutEstudiantes.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        layoutDocentes.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        // Click para expandir el card de Institucional
        expandInstitucional.setOnClickListener {
            val v = if (detailsTextInstitucional.visibility == View.GONE) View.VISIBLE else View.GONE
            detailsTextInstitucional.visibility = v
            documentsRecyclerViewInstitucional.visibility = if (documentsRecyclerViewInstitucional.visibility == View.GONE) View.VISIBLE else View.GONE

            if (documentsRecyclerViewInstitucional.visibility == View.VISIBLE) {
                cargarDocumentos("Institucional") // Cargar documentos de Institucional
            }
        }

        // Click para expandir el card de Estudiantes
        expandEstudiantes.setOnClickListener {
            val v = if (detailsTextEstudiantes.visibility == View.GONE) View.VISIBLE else View.GONE
            detailsTextEstudiantes.visibility = v
            documentsRecyclerViewEstudiantes.visibility = if (documentsRecyclerViewEstudiantes.visibility == View.GONE) View.VISIBLE else View.GONE

            if (documentsRecyclerViewEstudiantes.visibility == View.VISIBLE) {
                cargarDocumentos("Estudiantes") // Cargar documentos de Estudiantes
            }
        }

        // Click para expandir el card de Docentes
        expandDocentes.setOnClickListener {
            val v = if (detailsTextDocentes.visibility == View.GONE) View.VISIBLE else View.GONE
            detailsTextDocentes.visibility = v
            documentsRecyclerViewDocentes.visibility = if (documentsRecyclerViewDocentes.visibility == View.GONE) View.VISIBLE else View.GONE

            if (documentsRecyclerViewDocentes.visibility == View.VISIBLE) {
                cargarDocumentos("Docentes") // Cargar documentos de Docentes
            }
        }
    }

    // Modifica esta función para aceptar un tipo como parámetro
    private fun cargarDocumentos(type: String) {
        db.collection("documentos")
            .whereEqualTo("type", type) // Filtrar por el tipo especificado
            .get()
            .addOnSuccessListener { documents ->
                val documentList = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    val title = document.getString("title") ?: "Sin título"
                    val link = document.getString("link") ?: ""
                    documentList.add(Pair(title, link))
                }
                mostrarDocumentos(type, documentList) // Llama a la función para mostrar documentos
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar documentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDocumentos(type: String, documentList: MutableList<Pair<String, String>>) {
        when (type) {
            "Institucional" -> {
                documentsRecyclerViewInstitucional.layoutManager = LinearLayoutManager(this)
                documentsRecyclerViewInstitucional.adapter = DocumentAdapter(documentList)
            }
            "Estudiantes" -> {
                documentsRecyclerViewEstudiantes.layoutManager = LinearLayoutManager(this)
                documentsRecyclerViewEstudiantes.adapter = DocumentAdapter(documentList)
            }
            "Docentes" -> {
                documentsRecyclerViewDocentes.layoutManager = LinearLayoutManager(this)
                documentsRecyclerViewDocentes.adapter = DocumentAdapter(documentList)
            }
        }
    }
}
