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

class FifthFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fifth, container, false)
        val stepTitleTextView: TextView = view.findViewById(R.id.step_title5)
        val stepDescriptionTextView: TextView = view.findViewById(R.id.description_place5)
        val gifImageView: ImageView = view.findViewById(R.id.gif_destination5)
        val destination_name: TextView = view.findViewById(R.id.destination_name)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val locationName = arguments?.getString("location_name") ?: ""
        val start_location = arguments?.getString("start_location") ?: ""

        db.collection("ubicaciones")
            .whereEqualTo("name", locationName).whereEqualTo("start_location", start_location)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val stepTitle = document.getString("step_5_title") ?: ""
                    val stepDescription = document.getString("step_5_description") ?: ""
                    destination_name.text = name
                    stepTitleTextView.text = stepTitle
                    stepDescriptionTextView.text = stepDescription

                    val gifUri = document.getString("video_uri_5") ?: ""
                    if (gifUri.isNotEmpty()) {
                        val gifRef = storage.reference.child("videos/$gifUri")
                        gifRef.downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(this@FifthFragment)
                                .asGif()
                                .load(uri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(gifImageView)
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "No se encontró el GIF", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "No se encontró el GIF", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }

        return view
    }
}
