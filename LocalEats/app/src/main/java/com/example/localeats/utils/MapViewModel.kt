package com.example.localeats.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localeats.data.PlaceRepository
import com.example.localeats.model.MapUiState
import com.example.localeats.model.PlaceDetails
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {
    private val repository = PlaceRepository()

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    fun updateCamera(target: LatLng, zoom: Float) {
        _uiState.value = _uiState.value.copy(
            cameraTarget = target,
            zoom = zoom
        )
    }

    fun setPlaces(places: List<Place>) {
        _uiState.value = _uiState.value.copy(
            places = places
        )
    }

    fun selectPlace(place: PlaceDetails) {
        _uiState.value = _uiState.value.copy(
            selectedPlace = place
        )
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedPlace = null
        )
    }

    fun setSearchMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isSearchMode = enabled
        )
    }

    fun loadPlaces(apiKey: String, loc: LatLng, ) {
        repository.getNearbyPlaces(apiKey, loc.latitude, loc.longitude) { places ->
            Log.e("VIEWMODEL", "Loaded ${places.size} - first: $(it.firstOrNull()?.name}")
            _uiState.value = _uiState.value.copy(places = places)
            // _nearbyPlaces.postValue(places)
        }
    }

    fun loadPlaceDetails(apiKey: String, placeId: String) {
        repository.fetchPlaceDetails(apiKey, placeId) { result ->
            result?.let {
                _uiState.value = _uiState.value.copy(
                    selectedPlace = it
                )
            }
        }
    }

    fun searchPlaces(apiKey: String, query: String) {
        repository.searchPlaces(apiKey, query) { results ->
            _uiState.value = _uiState.value.copy(
                places = results,
                isSearchMode = true
            )
        }
    }
}