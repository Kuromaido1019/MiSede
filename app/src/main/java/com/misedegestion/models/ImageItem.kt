package com.misedegestion.models

data class ImageItem(
    val id: String,
    val resource: Int, // ID del recurso drawable
    val isSelected: Boolean,
    val name: String
)
