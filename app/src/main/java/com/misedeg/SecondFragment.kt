package com.misedeg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SecondFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        val stepTitleTextView: TextView = view.findViewById(R.id.step_title2)
        val stepDescriptionTextView: TextView = view.findViewById(R.id.description_place2)
        val gifImageView: ImageView = view.findViewById(R.id.gif_destination2)
        val destination_name: TextView = view.findViewById(R.id.destination_name)

        // Inicializar Firestore y Storage
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Obtener el nombre de la ubicación desde los argumentos
        val locationName = arguments?.getString("location_name") ?: ""
        val start_location = arguments?.getString("start_location") ?: ""
        Log.d("Fragment", "Start Location: $start_location")
        Log.d("Fragment", "Location Name: $locationName")

        // Consultar la colección 'ubicaciones' en Firestore
        db.collection("ubicaciones")
            .whereEqualTo("name", locationName).whereEqualTo("start_location", start_location)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Cargar los datos del primer paso en los TextViews
                    val name = document.getString("name") ?: ""
                    val stepTitle = document.getString("step_2_title") ?: ""
                    val stepDescription = document.getString("step_2_description") ?: ""
                    destination_name.text = name
                    stepTitleTextView.text = stepTitle
                    stepDescriptionTextView.text = stepDescription

                    // Cargar el GIF desde Firebase Storage
                    val gifUri = document.getString("video_uri_2") ?: ""
                    if (gifUri.isNotEmpty()) {
                        val gifRef = storage.reference.child("videos/$gifUri")
                        gifRef.downloadUrl.addOnSuccessListener { uri ->
                            // Cargar el GIF en el ImageView con Glide
                            Glide.with(this@SecondFragment)
                                .asGif()
                                .load(uri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(gifImageView)
                        }.addOnFailureListener {
                            // Mostrar un Toast si no se encuentra el GIF
                            Toast.makeText(requireContext(), "No se encontró el GIF", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Mostrar un Toast si el URI del GIF está vacío
                        Toast.makeText(requireContext(), "No se encontró el GIF", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                // Manejar el error al consultar Firestore
                Toast.makeText(requireContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }

        return view
    }
}
