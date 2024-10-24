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
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destino)

        firestore = FirebaseFirestore.getInstance()

        destinoTextView = findViewById(R.id.lbl_name_destination)
        partidaTextView = findViewById(R.id.lbl_name_start)
        destinoTextView.text = "¿A dónde quieres ir?"
        partidaTextView.text = "¿Dónde quieres empezar?"

        val imageDestinoRV = findViewById<RecyclerView>(R.id.destinosRV)
        val imageListDestino = arrayListOf<ImageItem>()
        val imageAdapterDestino = ImageAdapter() // Crear el adaptador una vez
        imageAdapterDestino.setOnItemClickListener(this) // Establecer el listener
        imageDestinoRV.adapter = imageAdapterDestino // Establecer el adaptador

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

        imageListDestino.addAll(destinos)
        imageAdapterDestino.submitList(imageListDestino)

        val imagePartidaRV = findViewById<RecyclerView>(R.id.partidaRV)
        val imageListPartida = arrayListOf<ImageItem>()
        val imageAdapterPartida = ImageAdapter()
        imageAdapterPartida.setOnItemClickListener(this)
        imagePartidaRV.adapter = imageAdapterPartida

        val partidas = listOf(
            ImageItem(UUID.randomUUID().toString(), R.drawable.german_1, false, "Calle Germán Riesco"),
            ImageItem(UUID.randomUUID().toString(), R.drawable.ocarro_1, false, "Calle Ocarrol")
        )

        imageListPartida.addAll(partidas)
        imageAdapterPartida.submitList(imageListPartida)


        val button = findViewById<Button>(R.id.btnStart)
        button.setOnClickListener {
            val selectedDestination = destinoTextView.text.toString()
            val selectedStart = partidaTextView.text.toString()

            if (selectedDestination.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona un destino antes de continuar.", Toast.LENGTH_SHORT).show()
            } else if (selectedStart.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona un lugar de inicio antes de continuar.", Toast.LENGTH_SHORT).show()
            } else {
                val locationName = selectedDestination.trim()
                val startLocation = selectedStart.trim().removePrefix("Calle ")

                validateLocations(locationName, startLocation) { exists, stepsValue ->
                    if (exists) {
                        steps = stepsValue
                        start = "start"

                        val intent = Intent(this, Recorrer::class.java)
                        intent.putExtra("location_name", locationName)
                        intent.putExtra("start_location", startLocation)
                        intent.putExtra("steps", steps) // Pasar el valor correcto de steps
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "La ubicación seleccionada o el lugar de inicio no existen.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateLocations(destinationName: String, startLocation: String, callback: (Boolean, Int) -> Unit) {
        firestore.collection("ubicaciones")
            .whereEqualTo("name", destinationName)
            .whereEqualTo("start_location", startLocation)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var stepsValue = 1
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        stepsValue = document.getLong("steps")?.toInt() ?: 1
                        Log.d("Destino", "Steps para $destinationName: $stepsValue")
                    }
                }
                callback(!querySnapshot.isEmpty, stepsValue)
            }
            .addOnFailureListener { exception ->
                callback(false, 1)
            }
    }

    override fun onItemClick(locationName: String) {
        if (locationName in listOf("Biblioteca", "Casino", "DAE", "Sala de Docencia", "Sala 401", "Información Financiera", "Admisión", "Laboratorios de Informática", "Oficina del Área de Educación")) {
            destinoTextView.text = locationName
        } else {
            partidaTextView.text = locationName
        }
    }
}
