package com.misedegestion

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

    // Variables para los elementos de la interfaz y Firebase
    private lateinit var txtTituloAvisoA: EditText
    private lateinit var txtFechaAvisoA: EditText
    private lateinit var txtDescripAvisoA: EditText
    private lateinit var spinnerCatAvisoA: Spinner
    private lateinit var btnImagenAvisoA: Button
    private lateinit var btnAgregarAvisoA: Button

    private var imagenUri: Uri? = null  // URI de la imagen seleccionada
    private val db = FirebaseFirestore.getInstance()  // Instancia de Firestore
    private val storage = FirebaseStorage.getInstance()  // Instancia de Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Activar diseño "edge to edge"
        setContentView(R.layout.activity_agregar_aviso)

        // Inicializar las vistas
        txtTituloAvisoA = findViewById(R.id.txtTituloAvisoA)
        txtFechaAvisoA = findViewById(R.id.txtFechaAvisoA)
        txtDescripAvisoA = findViewById(R.id.txtDescripAvisoA)
        spinnerCatAvisoA = findViewById(R.id.spinnerCatAvisoA)
        btnImagenAvisoA = findViewById(R.id.btnImagenAvisoA)
        btnAgregarAvisoA = findViewById(R.id.btnAgregarAvisoA)

        // Configurar spinner de categorías
        configurarSpinner()

        // Listener para seleccionar imagen
        btnImagenAvisoA.setOnClickListener {
            seleccionarImagen()
        }

        // Listener para mostrar el DatePicker
        txtFechaAvisoA.setOnClickListener {
            mostrarDatePicker()
        }

        // Listener para agregar aviso
        btnAgregarAvisoA.setOnClickListener {
            agregarAviso()
        }

        // Ajustar los insets del layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Método para configurar el spinner de categorías
    private fun configurarSpinner() {
        val categorias = arrayOf("Avisos Generales", "Avisos DAE", "Centro Aprendizaje")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCatAvisoA.adapter = adapter
    }

    // Método para seleccionar una imagen desde la galería
    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1000)  // Inicia la actividad para seleccionar imagen
    }

    // Método para mostrar el selector de fecha (DatePicker)
    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, yearSelected, monthOfYear, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth-${monthOfYear + 1}-$yearSelected"
            txtFechaAvisoA.setText(fechaSeleccionada)  // Actualiza el campo con la fecha seleccionada
        }, year, month, day)
        datePickerDialog.show()
    }

    // Método para subir imagen a Firebase Storage y agregar aviso a Firestore
    private fun agregarAviso() {
        val titulo = txtTituloAvisoA.text.toString().trim()
        val fecha = txtFechaAvisoA.text.toString().trim()
        val descripcion = txtDescripAvisoA.text.toString().trim()
        val categoria = spinnerCatAvisoA.selectedItem.toString()

        // Validar que todos los campos estén completos
        if (titulo.isEmpty() || fecha.isEmpty() || descripcion.isEmpty() || imagenUri == null) {
            Toast.makeText(this, "Por favor completa todos los campos y selecciona una imagen.", Toast.LENGTH_LONG).show()
            return
        }

        val nombreImagen = UUID.randomUUID().toString()  // Definir un nombre único para la imagen

        // Subir imagen a Firebase Storage
        val storageRef = storage.reference.child("images/$nombreImagen.png")
        storageRef.putFile(imagenUri!!)
            .addOnSuccessListener {
                // Agregar aviso a Firestore después de subir la imagen
                val aviso = mapOf(
                    "title" to titulo,
                    "date" to fecha,
                    "description" to descripcion,
                    "category" to categoria,
                    "cover" to nombreImagen  // Guardar el nombre de la imagen en Firestore
                )

                // Agregar el aviso a Firestore con ID generado automáticamente
                db.collection("noticias").add(aviso)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Aviso agregado exitosamente.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, AvisoAgregado::class.java)
                        startActivity(intent)  // Redirigir a la actividad de aviso agregado
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

    // Método para manejar el resultado de la selección de imagen
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            imagenUri = data?.data  // Guardar la URI de la imagen seleccionada
            Toast.makeText(this, "Imagen seleccionada.", Toast.LENGTH_SHORT).show()
        }
    }
}
