package com.misedeg

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditarAviso : AppCompatActivity() {

    private lateinit var txtTituloAvisoE: EditText
    private lateinit var txtFechaAvisoE: EditText
    private lateinit var txtDescrAvisoE: EditText
    private lateinit var spinnerCatAvisosE: Spinner
    private lateinit var btnImagenAvisosE: Button
    private lateinit var btnEditarAvisoE: Button
    private lateinit var spinnerAvisosE: Spinner

    private var imagenUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var avisoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_aviso)

        txtTituloAvisoE = findViewById(R.id.txtTituloAvisoE)
        txtFechaAvisoE = findViewById(R.id.txtFechaAvisoE)
        txtDescrAvisoE = findViewById(R.id.txtDescrAvisoE)
        spinnerCatAvisosE = findViewById(R.id.spinnerCatAvisosE)
        btnImagenAvisosE = findViewById(R.id.btnImagenAvisosE)
        btnEditarAvisoE = findViewById(R.id.btnEditarAvisoE)
        spinnerAvisosE = findViewById(R.id.spinnerAvisosE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarSpinnerCategorias()
        configurarSpinnerAvisos()

        txtFechaAvisoE.setOnClickListener {
            mostrarDatePicker()
        }

        btnImagenAvisosE.setOnClickListener {
            seleccionarImagen()
        }

        btnEditarAvisoE.setOnClickListener {
            editarAviso()
        }
    }

    private fun configurarSpinnerCategorias() {
        val categorias = arrayOf("Avisos Generales", "Avisos DAE", "Centro Aprendizaje")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCatAvisosE.adapter = adapter
    }

    private fun configurarSpinnerAvisos() {
        db.collection("noticias").get().addOnSuccessListener { querySnapshot ->
            val titulos = querySnapshot.documents.map { it.getString("title") ?: "" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, titulos)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAvisosE.adapter = adapter

            spinnerAvisosE.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedTitle = parent.getItemAtPosition(position) as String
                    cargarAvisoPorTitulo(selectedTitle)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al cargar los avisos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cargarAvisoPorTitulo(titulo: String) {
        db.collection("noticias").whereEqualTo("title", titulo).get().addOnSuccessListener { querySnapshot ->
            val document = querySnapshot.documents.firstOrNull()
            if (document != null) {
                avisoId = document.id
                val fecha = document.getString("date")
                val descripcion = document.getString("description")
                val categoria = document.getString("category")

                txtTituloAvisoE.setText(titulo)
                txtFechaAvisoE.setText(fecha)
                txtDescrAvisoE.setText(descripcion)
                spinnerCatAvisosE.setSelection((spinnerCatAvisosE.adapter as ArrayAdapter<String>).getPosition(categoria))

            } else {
                Toast.makeText(this, "No se encontró el aviso.", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al cargar el aviso: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, yearSelected, monthOfYear, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth-${monthOfYear + 1}-$yearSelected"
            txtFechaAvisoE.setText(fechaSeleccionada)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1001)
    }

    private fun editarAviso() {
        val titulo = txtTituloAvisoE.text.toString().trim()
        val fecha = txtFechaAvisoE.text.toString().trim()
        val descripcion = txtDescrAvisoE.text.toString().trim()
        val categoria = spinnerCatAvisosE.selectedItem.toString()

        if (titulo.isEmpty() || fecha.isEmpty() || descripcion.isEmpty() || avisoId == null) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        db.collection("noticias").document(avisoId!!).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val datosAviso = document.data ?: return@addOnSuccessListener
                    val nombreImagen = datosAviso["cover"] as? String ?: ""

                    val aviso = mapOf(
                        "title" to titulo,
                        "date" to fecha,
                        "description" to descripcion,
                        "category" to categoria
                    )

                    db.collection("noticias").document(avisoId!!).update(aviso)
                        .addOnSuccessListener {
                            if (imagenUri != null) {
                                val storageRef = storage.reference.child("images/$nombreImagen.png")
                                storageRef.putFile(imagenUri!!)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Aviso editado exitosamente.", Toast.LENGTH_LONG).show()
                                        redirigirAvisoActualizado()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al subir la imagen: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                Toast.makeText(this, "Aviso editado exitosamente.", Toast.LENGTH_LONG).show()
                                redirigirAvisoActualizado()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al editar el aviso: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el aviso: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun redirigirAvisoActualizado() {
        val intent = Intent(this, AvisoActualizado::class.java)
        startActivity(intent)
        finish()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            imagenUri = data?.data
            Toast.makeText(this, "Imagen seleccionada.", Toast.LENGTH_SHORT).show()
        }
    }
}
