package com.misedeg

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.misedeg.databinding.CardCellBinding
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class CardViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: NoticeClickListener
) : RecyclerView.ViewHolder(cardCellBinding.root) {

    private val storage = FirebaseStorage.getInstance()

    fun bindNotice(notice: Noticia) {
        val imageNameWithExtension = "${notice.cover}.png"

        val storageRef = storage.reference.child("images/$imageNameWithExtension")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(cardCellBinding.cover)
        }.addOnFailureListener {
        }

        cardCellBinding.title.text = notice.title
        cardCellBinding.author.text = notice.date

        cardCellBinding.cardView.setOnClickListener {
            clickListener.onClick(notice)
        }
    }
}
