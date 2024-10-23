package com.misedegestion

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misedegestion.databinding.CardCellBinding

class CardAdapter(
    private val notices: List<Noticia>,
    private val clickListener: NoticeClickListener,
    private val context: Context
) : RecyclerView.Adapter<CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CardCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val notice = notices[position]
        holder.bindNotice(notice)
    }

    override fun getItemCount() = notices.size
}