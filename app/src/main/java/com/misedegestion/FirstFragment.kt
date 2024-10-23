package com.misedegestion

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirstFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var count_step: TextView
    private lateinit var percent_destination: TextView
    private lateinit var progress_destination: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        val stepTitleTextView: TextView = view.findViewById(R.id.step_title1)
        val stepDescriptionTextView: TextView = view.findViewById(R.id.description_place1)
        val videoView: VideoView = view.findViewById(R.id.video_destination)
        val destination_name: TextView = view.findViewById(R.id.destination_name)

        // Inicializar Firestore y Storage
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Obtener el nombre de la ubicación desde los argumentos
        val locationName = arguments?.getString("location_name") ?: ""
        val start_location = arguments?.getString("start_location") ?: ""
        Log.d("FirstFragment", "Start Location: $start_location")
        Log.d("FirstFragment", "Location Name: $locationName")

        // Consultar la colección 'ubicaciones' en Firestore
        db.collection("ubicaciones")
            .whereEqualTo("name", locationName).whereEqualTo("start_location", start_location)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Cargar los datos del primer paso en los TextViews
                    val name = document.getString("name") ?: ""
                    val stepTitle = document.getString("step_1_title") ?: ""
                    val stepDescription = document.getString("step_1_description") ?: ""
                    destination_name.text = name
                    stepTitleTextView.text = stepTitle
                    stepDescriptionTextView.text = stepDescription

                    // Cargar el video desde Firebase Storage
                    val videoUri = document.getString("video_uri_1") ?: ""
                    if (videoUri.isNotEmpty()) {
                        val videoRef = storage.reference.child("videos/$videoUri")
                        videoRef.downloadUrl.addOnSuccessListener { uri ->
                            videoView.setVideoURI(uri)
                            videoView.start()
                            videoView.setOnPreparedListener { mediaPlayer ->
                                mediaPlayer.isLooping = true
                            }
                        }.addOnFailureListener {
                            // Mostrar un Toast si no se encuentra el video
                            Toast.makeText(requireContext(), "No se encontró el video", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Mostrar un Toast si el video URI está vacío
                        Toast.makeText(requireContext(), "No se encontró el video", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                // Manejar el error al consultar Firestore
                Toast.makeText(requireContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }

        return view
    }
}
