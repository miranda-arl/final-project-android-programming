package com.example.localeats.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place


data class MapUiState(
    val cameraTarget: LatLng? = null,
    val zoom: Float = 14f,
    val places: List<Place> = emptyList(),
    val selectedPlace: PlaceDetails? = null,
    val isSearchMode: Boolean = false
)