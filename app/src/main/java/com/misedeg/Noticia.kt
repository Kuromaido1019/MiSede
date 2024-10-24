package com.misedeg

data class Noticia(
    val cover: String = "",
    val title: String = "",
    val date: String = "",
    val description: String = "",
    var link: String = "",
    var id: String? = null // Cambia esto a var para hacerlo mutable
)
