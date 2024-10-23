package com.misedegestion

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DocumentAdapter(private val documentList: List<Pair<String, String>>) :
    RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.documentTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val (title, link) = documentList[position]
        holder.titleTextView.text = title

        // Manejar el clic en el título del documento
        holder.itemView.setOnClickListener {
            // Abrir el enlace en un navegador o en un WebView
            val context = holder.itemView.context
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(link)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return documentList.size
    }
}

