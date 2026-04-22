package com.example.localeats.data

data class Review(
    val userId: String = "",
    val userName: String = "",
    val comment: String = "",
    val rating: Float = 0f,
    val placeId: String = "",
    val imageUrl: String = ""
)