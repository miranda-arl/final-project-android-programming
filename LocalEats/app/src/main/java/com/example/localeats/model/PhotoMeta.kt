package com.example.localeats.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoMeta(
    var ownerName: String = "",
    var ownerUid: String = "",
    var uuid: String = "",
    var byteSize: Long = 0L,
    var pictureTitle: String = "",
    @ServerTimestamp val timeStamp: Timestamp? = null,
    @DocumentId var firestoreID: String = ""
) : Parcelable