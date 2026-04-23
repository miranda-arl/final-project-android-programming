package com.example.localeats.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Review(
    val userId: String = "",
    val userName: String = "",
    val comment: String = "",
    val rating: Float = 0f,
    val placeId: String = "",
    val imageUUID: String = "",
    @ServerTimestamp
    var timeStamp: Timestamp? = null
)