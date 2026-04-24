package com.example.localeats.model

sealed class ReviewEvent {
    data class Submit(
        val rating: Float,
        val comment: String,
        val imageUUID: String
    ) : ReviewEvent()
}