package com.example.localeats.model

import com.google.android.gms.maps.model.LatLng

data class PlaceDetails(
    val id: String,
    val name: String,
    val address: String,
    val latLng: LatLng?
)