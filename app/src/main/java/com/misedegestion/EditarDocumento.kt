package com.misedegestion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Suppress("DEPRECATION")
class EditarDocumento : AppCompatActivity() {

    private lateinit var spinnerDocumentos: Spinner
    private lateinit var txtNewTituloDoc: EditText
    private lateinit var txtNewEnlaceDoc: EditText
    private lateinit var btnImagenDocUpdate: Button
    private lateinit var btnActualizarDocumento: Button
    private lateinit var spnTipoDocEdi: Spinner // Spinner para las categorías

    private var imagenUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var documentoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_documento)

        // Inicializar vistas
        spinnerDocumentos = findViewById(R.id.spinnerDocumentos)
        txtNewTituloDoc = findViewById(R.id.txtNewTituloDoc)
        txtNewEnlaceDoc = findViewById(R.id.txtNewEnlaceDoc)
        btnImagenDocUpdate = findViewById(R.id.btnImagenDocUpdate)
        btnActualizarDocumento = findViewById(R.id.btnActualizarDocumento)
        spnTipoDocEdi = findViewById(R.id.spnTipoDocEdi) // Inicializa el spinner de categorías

        // Configurar el spinner de documentos existentes
        configurarSpinnerDocumentos()

        // Configurar el spinner de categorías
        configurarSpinnerCategorias()

        // Seleccionar imagen
        btnImagenDocUpdate.setOnClickListener {
            seleccionarImagen()
        }

        // Actualizar documento
        btnActualizarDocumento.setOnClickListener {
            actualizarDocumento()
        }

        // Ajustar insets para la UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Método para configurar el spinner con los documentos existentes
    private fun configurarSpinnerDocumentos() {
        db.collection("documentos").get().addOnSuccessListener { querySnapshot ->
            val documentos = querySnapshot.map { it.getString("title") ?: "" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, documentos)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDocumentos.adapter = adapter

            spinnerDocumentos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    // Obtener ID del documento seleccionado
                    documentoId = querySnapshot.documents[position].id
                    // Cargar datos del documento en campos EditText
                    cargarDatosDocumento(documentoId!!)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    // Método para cargar datos del documento seleccionado
    private fun cargarDatosDocumento(id: String) {
        db.collection("documentos").document(id).get().addOnSuccessListener { document ->
            if (document != null) {
                txtNewTituloDoc.setText(document.getString("title"))
                txtNewEnlaceDoc.setText(document.getString("link"))
                // Cargar el tipo del documento en el spinner
                val tipoDoc = document.getString("type")
                val posicionTipo = obtenerPosicionTipo(tipoDoc)
                spnTipoDocEdi.setSelection(posicionTipo)
                // También puedes cargar la imagen aquí si es necesario
            } else {
                Toast.makeText(this, "Documento no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para obtener la posición del tipo en el spinner
    private fun obtenerPosicionTipo(tipo: String?): Int {
        return when (tipo) {
            "Institucional" -> 0
            "Estudiantes" -> 1
            "Docentes" -> 2
            else -> 0 // Por defecto a "Institucional" si no coincide
        }
    }

    // Método para configurar el spinner de categorías
    private fun configurarSpinnerCategorias() {
        val categorias = listOf("Institucional", "Estudiantes", "Docentes")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipoDocEdi.adapter = adapter
    }

    // Método para seleccionar imagen
    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    // Manejar el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imagenUri = data?.data
            // Puedes mostrar la imagen en un ImageView si lo deseas
        }
    }

    // Método para actualizar el documento
    private fun actualizarDocumento() {
        val nuevoTitulo = txtNewTituloDoc.text.toString()
        val nuevoEnlace = txtNewEnlaceDoc.text.toString()
        val nuevoTipo = spnTipoDocEdi.selectedItem.toString() // Obtener tipo del spinner

        // Validar campos
        if (nuevoTitulo.isEmpty() || nuevoEnlace.isEmpty() || documentoId == null) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar título, enlace y tipo en Firestore
        val documentoActualizado = hashMapOf(
            "title" to nuevoTitulo,
            "link" to nuevoEnlace,
            "type" to nuevoTipo // Actualizar el tipo también
        )

        db.collection("documentos").document(documentoId!!).update(documentoActualizado as Map<String, Any>).addOnSuccessListener {
            // Actualizar imagen en Firebase Storage si se seleccionó
            if (imagenUri != null) {
                val refStorage = storage.reference.child("images/doc_$documentoId")
                refStorage.putFile(imagenUri!!)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Documento actualizado", Toast.LENGTH_SHORT).show()
                        finish() // O redirige a otra actividad
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Documento actualizado sin cambios en la imagen", Toast.LENGTH_SHORT).show()
                finish() // O redirige a otra actividad
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al actualizar el documento: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
