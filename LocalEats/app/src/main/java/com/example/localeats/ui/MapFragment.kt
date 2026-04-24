package com.example.localeats.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.localeats.R
import com.example.localeats.model.PlaceDetails
import com.example.localeats.utils.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var viewModel: MapViewModel

    private val API_KEY by lazy {
        getString(R.string.google_maps_key)
    }

//    private var lastCameraPosition: LatLng? = null
//    private var lastZoom: Float = 14f
    private var lastRenderedCamera: LatLng? = null
    private var lastRenderedZoom: Float? = null

    private var lastPlaces: List<Place>? = null

    private var lastSelectedPlaceId: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentMarkers = mutableListOf<com.google.android.gms.maps.model.Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

/*
        viewModel.nearbyPlaces.observe(viewLifecycleOwner) { places ->

            if (!::googleMap.isInitialized) return@observe

            currentMarkers.forEach { it.remove() }
            currentMarkers.clear()

            places.forEach { place ->
                place.latLng?.let { latLng ->
                    // Log.e("MAP", "adding marker for ${place.name} at $latLng")
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(place.name ?: "Unknown place")
                            // .snippet(place.address ?: "")
                    )

                    val placeUI = PlaceDetails(
                        id = place.id ?: "",
                        name = place.name ?: "",
                        address = place.address ?: "Unknown address",
                        latLng = latLng
                    )

                    marker?.tag = placeUI
                    // marker?.tag = place // ?: "" //.id
                    marker?.showInfoWindow()
                    if (marker != null) currentMarkers.add(marker)
                }
            }
        }

        viewModel.selectedPlace.observe(viewLifecycleOwner) { place ->
            place.latLng?.let {
                // googleMap.clear()

//                val marker = googleMap.addMarker(
//                    MarkerOptions()
//                        .position(it)
//                        .title(place.name)
//                )
//
//                marker?.tag = place.id

//                googleMap.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(it, 15f)
//                )
            }
        }

        viewModel.selectedPlaceDetails.observe(viewLifecycleOwner) { details ->
            Log.d("UI", "Showing: ${details.name}")

            if (!::googleMap.isInitialized) return@observe

            //googleMap.clear()

            details.latLng?.let { latLng ->

                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(details.name)
                )

                marker?.tag = details // .id
                marker?.showInfoWindow()

//                googleMap.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(latLng, 15f)
//                )
            }
        }
*/

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->

                if (!::googleMap.isInitialized) return@collect

                // Restore camera
                state.cameraTarget?.let { target ->
                    if (target != lastRenderedCamera || state.zoom != lastRenderedZoom) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(target, state.zoom)
                        )
                        lastRenderedCamera = target
                        lastRenderedZoom = state.zoom
                    }
                }

                if (state.places != lastPlaces) {
                    // Update markers
                    currentMarkers.forEach { it.remove() }
                    currentMarkers.clear()

                    state.places.forEach { place ->
                        place.latLng?.let { latLng ->
                            val marker = googleMap.addMarker(
                                MarkerOptions().position(latLng).title(place.name)
                            )

                            val placeUI = PlaceDetails(
                                id = place.id ?: "",
                                name = place.name ?: "",
                                address = place.address ?: "",
                                latLng = latLng
                            )

                            marker?.tag = placeUI
                            if (marker != null) currentMarkers.add(marker)
                            marker?.showInfoWindow()
                        }
                    }
                    lastPlaces = state.places
                }

                // Focus selected place
                state.selectedPlace?.let { selected ->
                    // val alreadyExists = state.places.any { it.id == selected.id }

                    if (selected.latLng != null) {
                        // Update markers
                        currentMarkers.forEach { it.remove() }
                        currentMarkers.clear()

                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(selected.latLng)
                                .title(selected.name)
                        )

                        marker?.tag = selected
                        if (marker != null) currentMarkers.add(marker)
                        marker?.showInfoWindow()
                    }

                    if (selected.id != lastSelectedPlaceId) {
                        selected.latLng?.let {
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(it, 15f)
                            )
                        }
                        lastSelectedPlaceId = selected.id
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // isMapReady = true

        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        } else {
            if (viewModel.uiState.value.cameraTarget == null) {
                fetchLocationAndLoadPlaces()
            }
        }

        googleMap.setOnCameraIdleListener {
            val cam = googleMap.cameraPosition
            viewModel.updateCamera(cam.target, cam.zoom)
        }

        googleMap.setOnMarkerClickListener { marker ->
            val place = marker.tag as? PlaceDetails ?: return@setOnMarkerClickListener true
            viewModel.selectPlace(place)

            findNavController().navigate(
                MapFragmentDirections
                    .actionMapFragmentToRestaurantDetailFragment(
                        place.id,
                        place.name,
                        place.address
                    )
            )
            // viewModel.clearSelection()
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndLoadPlaces()
        } else {
            Toast.makeText(requireContext(),
                "Location permission required to load places",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchLocationAndLoadPlaces() {
        getUserLocation { latLng ->
            viewModel.updateCamera(latLng, 14f)
            viewModel.loadPlaces(API_KEY, latLng)
        }
    }

    private fun getUserLocation(onResult: (LatLng) -> Unit) {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                Log.e("LOCATION", "loc: $location")
                if (location != null) {
                    onResult(LatLng(location.latitude, location.longitude))
                } else {
                    // fallback to fresh request
                    fusedLocationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { freshLocation ->

                        if (freshLocation != null) {
                            onResult(LatLng(freshLocation.latitude, freshLocation.longitude))
                        } else {
                            onResult(LatLng(37.7749, -122.4194))
                        }
                    }
                }
            }
            .addOnFailureListener {
                onResult(LatLng(37.7749, -122.4194))
            }
    }
}