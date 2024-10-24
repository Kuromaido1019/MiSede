package com.misedeg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.misedeg.adapters.ImageAdapter
import com.misedeg.models.ImageItem
import java.util.UUID

class Destino : AppCompatActivity(), ImageAdapter.OnItemClickListener {

    private lateinit var destinoTextView: TextView
    private lateinit var partidaTextView: TextView
    private var steps: Int = 1
    private var start: String = ""
    private lateinit var firestore: FirebaseFirestore // Declarar la variable Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destino)

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Configuración de textviews para nombres de ubicaciones
        destinoTextView = findViewById(R.id.lbl_name_destination)
        partidaTextView = findViewById(R.id.lbl_name_start)
        destinoTextView.text = "¿A dónde quieres ir?"
        partidaTextView.text = "¿Dónde quieres empezar?"

        // Configuración del RecyclerView Destino
        val imageDestinoRV = findViewById<RecyclerView>(R.id.destinosRV)
        val imageListDestino = arrayListOf<ImageItem>()
        val imageAdapterDestino = ImageAdapter() // Crear el adaptador una vez
        imageAdapterDestino.setOnItemClickListener(this) // Establecer el listener
        imageDestinoRV.adapter = imageAdapterDestino // Establecer el adaptador

        // Ubicaciones de destino
        val destinos = listOf(
            ImageItem(UUID.randomUUID().toString(), R.drawable.biblioteca_2, false, "Biblioteca"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.comedor_1, false, "Casino"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.dae_1, false, "DAE"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.docencia_1, false, "Sala de Docencia"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.sala401_1, false, "Sala 401"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.info_financiera_1, false, "Información Financiera"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.admision_1, false, "Admisión"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.lab_informatica_1, false, "Laboratorios de Informática"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.oficina_educacion_1, false, "Oficina del Área de Educación")
        )

        // Cargar las imágenes desde drawable para destino
        imageListDestino.addAll(destinos) // Agregar las imágenes a la lista
        imageAdapterDestino.submitList(imageListDestino) // Actualizar el adaptador

        // Configuración del RecyclerView Partida
        val imagePartidaRV = findViewById<RecyclerView>(R.id.partidaRV)
        val imageListPartida = arrayListOf<ImageItem>()
        val imageAdapterPartida = ImageAdapter() // Crear el adaptador una vez
        imageAdapterPartida.setOnItemClickListener(this) // Establecer el listener
        imagePartidaRV.adapter = imageAdapterPartida // Establecer el adaptador

        // Ubicaciones de partida
        val partidas = listOf(
            ImageItem(UUID.randomUUID().toString(), R.drawable.german_1, false, "Calle Germán Riesco"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.ocarro_1, false, "Calle Ocarrol")
        )

        // Cargar las imágenes desde drawable para partida
        imageListPartida.addAll(partidas) // Agregar las imágenes a la lista
        imageAdapterPartida.submitList(imageListPartida) // Actualizar el adaptador

        // Redirigir a Recorrer
        val button = findViewById<Button>(R.id.btnStart)
        button.setOnClickListener {
            // Verificar si se han seleccionado un destino y una partida
            val selectedDestination = destinoTextView.text.toString()
            val selectedStart = partidaTextView.text.toString()

            Log.d("Destino", "Destino seleccionado: $selectedDestination")
            Log.d("Destino", "Partida seleccionada: $selectedStart")

            if (selectedDestination.isEmpty()) {
                // Mostrar un mensaje de error si no se seleccionó destino
                Toast.makeText(this, "Por favor, selecciona un destino antes de continuar.", Toast.LENGTH_SHORT).show()
            } else if (selectedStart.isEmpty()) {
                // Mostrar un mensaje de error si no se seleccionó partida
                Toast.makeText(this, "Por favor, selecciona un lugar de inicio antes de continuar.", Toast.LENGTH_SHORT).show()
            } else {
                // Extraer los nombres de ubicación seleccionada
                val locationName = selectedDestination.trim()
                val startLocation = selectedStart.trim().removePrefix("Calle ") // Remover el prefijo "Calle"

                Log.d("Destino", "Validando ubicación: $locationName con inicio: $startLocation")

                // Validar si ambas ubicaciones existen en Firestore
                validateLocations(locationName, startLocation) { exists, stepsValue ->
                    if (exists) {
                        // Actualizar el valor de "steps" con el valor recuperado
                        steps = stepsValue
                        start = "start"
                        Log.d("Destino", "Start: $startLocation")

                        // Si ambas ubicaciones existen, iniciar la actividad Recorrer
                        val intent = Intent(this, Recorrer::class.java)
                        intent.putExtra("location_name", locationName)
                        intent.putExtra("start_location", startLocation)
                        intent.putExtra("steps", steps) // Pasar el valor correcto de steps
                        startActivity(intent)
                    } else {
                        // Mostrar un mensaje de error si alguna ubicación no existe
                        Toast.makeText(this, "La ubicación seleccionada o el lugar de inicio no existen.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateLocations(destinationName: String, startLocation: String, callback: (Boolean, Int) -> Unit) {
        Log.d("Destino", "Consultando Firestore para: destinationName = $destinationName, startLocation = $startLocation")

        firestore.collection("ubicaciones")
            .whereEqualTo("name", destinationName)
            .whereEqualTo("start_location", startLocation) // Verificar ambos campos
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("Destino", "Consulta exitosa, documentos encontrados: ${querySnapshot.size()}")

                var stepsValue = 1 // Valor por defecto

                if (!querySnapshot.isEmpty) {
                    // Iterar sobre los documentos encontrados
                    for (document in querySnapshot.documents) {
                        // Obtener el campo "steps"
                        stepsValue = document.getLong("steps")?.toInt() ?: 1 // Cambia "steps" si el campo tiene un nombre diferente
                        Log.d("Destino", "Steps para $destinationName: $stepsValue") // Imprimir en el log
                    }
                }

                callback(!querySnapshot.isEmpty, stepsValue) // Devuelve también el valor de los steps
            }
            .addOnFailureListener { exception ->
                // Manejo de errores, puedes imprimir el error si lo deseas
                Log.e("Destino", "Error al consultar Firestore: ${exception.message}")
                callback(false, 1) // Considera que no existe si hay un error, y usa el valor por defecto de steps
            }
    }

    override fun onItemClick(locationName: String) {
        // Cambiar el texto de las TextView sin agregar el prefijo "Ubicación:"
        if (locationName in listOf("Biblioteca", "Casino", "DAE", "Sala de Docencia", "Sala 401", "Información Financiera", "Admisión", "Laboratorios de Informática", "Oficina del Área de Educación")) {
            destinoTextView.text = locationName // Solo muestra el nombre
        } else {
            partidaTextView.text = locationName // Solo muestra el nombre
        }
    }
}
