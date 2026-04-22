package com.example.localeats.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localeats.data.PlaceRepository
import com.example.localeats.model.PlaceDetails
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

class MapViewModel : ViewModel() {
    private val repository = PlaceRepository()

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _nearbyPlaces = MutableLiveData<List<Place>>()
    val nearbyPlaces: LiveData<List<Place>> = _nearbyPlaces
    private val _searchResults = MutableLiveData<List<Place>>()
    val searchResults: LiveData<List<Place>> = _searchResults

    private val _selectedPlace = MutableLiveData<Place>()
    val selectedPlace: LiveData<Place> = _selectedPlace

    private val _selectedLatLng = MutableLiveData<LatLng>()
    val selectedLatLng: LiveData<LatLng> = _selectedLatLng

    private val _selectedPlaceDetails = MutableLiveData<PlaceDetails>()
    val selectedPlaceDetails: LiveData<PlaceDetails> = _selectedPlaceDetails

//    fun setPlaces(list: List<Place>) {
//        _places.value = list
//    }
//
//    fun setSelectedPlaceLatLng(latLng: LatLng) {
//        _selectedLatLng.value = latLng
//    }
//
//    fun setSelectedPlace(place: Place) {
//        _selectedPlace.value = place
//    }

    fun loadPlaces(apiKey: String, loc: LatLng, ) {
        repository.getNearbyPlaces(apiKey, loc.latitude, loc.longitude) {
            Log.e("VIEWMODEL", "Loaded ${it.size} - first: $(it.firstOrNull()?.name}")
            _nearbyPlaces.postValue(it)
        }
    }

    fun loadPlaceDetails(apiKey: String, placeId: String) {
        repository.fetchPlaceDetails(apiKey, placeId) { result ->
            result?.let {
                Log.d("VIEWMODEL", "Loaded: ${it.name} - ${it.address}")
                _selectedPlaceDetails.postValue(it)
                it.latLng?.let { latLng ->
                    _selectedLatLng.postValue(latLng)
                }
            }
        }
    }

    fun searchPlaces(apiKey: String, query: String) {
        repository.searchPlaces(apiKey, query) { results ->
            Log.d("VIEWMODEL", "Search returned ${results.size} results")
            _searchResults.postValue(results)
        }
    }
    fun selectPlace(details: PlaceDetails) {
        _selectedPlaceDetails.postValue(details)
        details.latLng?.let {
            _selectedLatLng.postValue(it)
        }
    }
}