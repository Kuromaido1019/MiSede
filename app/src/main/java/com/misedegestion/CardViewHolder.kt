package com.misedegestion

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.misedegestion.databinding.CardCellBinding
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class CardViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: NoticeClickListener
) : RecyclerView.ViewHolder(cardCellBinding.root) {

    private val storage = FirebaseStorage.getInstance()

    fun bindNotice(notice: Noticia) {
        // Agregar la extensión del archivo .png a la ruta de la imagen
        val imageNameWithExtension = "${notice.cover}.png"

        // Obtener la referencia a la imagen en Firebase Storage
        val storageRef = storage.reference.child("images/$imageNameWithExtension")

        // Obtener la URL de descarga de la imagen
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            // Cargar la imagen desde la URL obtenida
            Picasso.get().load(uri).into(cardCellBinding.cover)
        }.addOnFailureListener {
            Log.d("CardViewHolder", "Error al cargar la imagen")
        }

        cardCellBinding.title.text = notice.title
        cardCellBinding.author.text = notice.date

        cardCellBinding.cardView.setOnClickListener {
            Log.d("CardViewHolder", "Clicked on notice: ${notice.title} with ID: ${notice.id}")
            clickListener.onClick(notice)
        }
    }
}
