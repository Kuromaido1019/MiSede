package com.misedeg

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@Suppress("DEPRECATION")
class AgregarAviso : AppCompatActivity() {

    private lateinit var txtTituloAvisoA: EditText
    private lateinit var txtFechaAvisoA: EditText
    private lateinit var txtDescripAvisoA: EditText
    private lateinit var spinnerCatAvisoA: Spinner
    private lateinit var btnImagenAvisoA: Button
    private lateinit var btnAgregarAvisoA: Button

    private var imagenUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Activar diseño "edge to edge"
        setContentView(R.layout.activity_agregar_aviso)

        txtTituloAvisoA = findViewById(R.id.txtTituloAvisoA)
        txtFechaAvisoA = findViewById(R.id.txtFechaAvisoA)
        txtDescripAvisoA = findViewById(R.id.txtDescripAvisoA)
        spinnerCatAvisoA = findViewById(R.id.spinnerCatAvisoA)
        btnImagenAvisoA = findViewById(R.id.btnImagenAvisoA)
        btnAgregarAvisoA = findViewById(R.id.btnAgregarAvisoA)

        configurarSpinner()

        btnImagenAvisoA.setOnClickListener {
            seleccionarImagen()
        }

        txtFechaAvisoA.setOnClickListener {
            mostrarDatePicker()
        }

        btnAgregarAvisoA.setOnClickListener {
            agregarAviso()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarSpinner() {
        val categorias = arrayOf("Avisos Generales", "Avisos DAE", "Centro Aprendizaje")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCatAvisoA.adapter = adapter
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1000)  // Inicia la actividad para seleccionar imagen
    }

    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, yearSelected, monthOfYear, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth-${monthOfYear + 1}-$yearSelected"
            txtFechaAvisoA.setText(fechaSeleccionada)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun agregarAviso() {
        val titulo = txtTituloAvisoA.text.toString().trim()
        val fecha = txtFechaAvisoA.text.toString().trim()
        val descripcion = txtDescripAvisoA.text.toString().trim()
        val categoria = spinnerCatAvisoA.selectedItem.toString()

        if (titulo.isEmpty() || fecha.isEmpty() || descripcion.isEmpty() || imagenUri == null) {
            Toast.makeText(this, "Por favor completa todos los campos y selecciona una imagen.", Toast.LENGTH_LONG).show()
            return
        }

        val nombreImagen = UUID.randomUUID().toString()

        val storageRef = storage.reference.child("images/$nombreImagen.png")
        storageRef.putFile(imagenUri!!)
            .addOnSuccessListener {
                val aviso = mapOf(
                    "title" to titulo,
                    "date" to fecha,
                    "description" to descripcion,
                    "category" to categoria,
                    "cover" to nombreImagen
                )
                db.collection("noticias").add(aviso)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Aviso agregado exitosamente.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, AvisoAgregado::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al agregar el aviso: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir la imagen: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            imagenUri = data?.data
            Toast.makeText(this, "Imagen seleccionada.", Toast.LENGTH_SHORT).show()
        }
    }
}
