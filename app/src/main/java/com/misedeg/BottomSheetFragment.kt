package com.misedeg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.misedeg.adapters.ImageAdapter
import com.misedeg.models.ImageItem

class BottomSheetFragment : BottomSheetDialogFragment() {

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val ARG_NOMBRE_DESTINO = "nombre_destino"
        private const val ARG_DESCRIPCION_DESTINO = "descripcion_destino"

        fun newInstance(nombreDestino: String, descripcionDestino: String): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_NOMBRE_DESTINO, nombreDestino)
            args.putString(ARG_DESCRIPCION_DESTINO, descripcionDestino)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nombreDestino = arguments?.getString(ARG_NOMBRE_DESTINO) ?: "Destino desconocido"
        val descripcionDestino = arguments?.getString(ARG_DESCRIPCION_DESTINO) ?: "Descripción no disponible"

        val tituloSheet = view.findViewById<TextView>(R.id.titleSheet)
        val descripcionSheet = view.findViewById<TextView>(R.id.descriptionSheet)

        tituloSheet.text = nombreDestino
        descripcionSheet.text = descripcionDestino

        // Configuración del RecyclerView con imágenes
        val imageRV = view.findViewById<RecyclerView>(R.id.imageRV)
        val imageList = arrayListOf<ImageItem>()

        // Buscar la ubicación en Firestore usando el campo "name"
        firestore.collection("ubicaciones")
            .whereEqualTo("name", nombreDestino)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()

                    // Obtener los campos photo_1, photo_2, photo_3
                    val photo1 = document.getString("photo_1")
                    val photo2 = document.getString("photo_2")
                    val photo3 = document.getString("photo_3")

                    // Lista con los nombres de las imágenes
                    val imageNames = listOf(photo1, photo2, photo3).filterNotNull()

                    // Cargar las imágenes desde Firebase Storage
                    imageNames.forEach { imageName ->
                        val storageRef = storage.reference.child("places/$imageName")
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Agregar la imagen a la lista de imágenes
                            //imageList.add(ImageItem(UUID.randomUUID().toString(), uri.toString(),false,""))

                            // Actualizar el adaptador cuando se carguen las imágenes
                            val imageAdapter = ImageAdapter()
                            imageRV.adapter = imageAdapter
                            imageAdapter.submitList(imageList)
                        }.addOnFailureListener {
                            // Manejar errores si es necesario
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Manejar el error al buscar en Firestore
            }
    }
}
