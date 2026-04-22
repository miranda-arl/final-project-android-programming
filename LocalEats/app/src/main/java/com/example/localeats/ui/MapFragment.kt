package com.example.localeats.ui

import android.content.Intent
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
import com.example.localeats.R
import com.example.localeats.utils.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var viewModel: MapViewModel

    private val placesClient by lazy {
        Places.createClient(requireContext())
    }

    private val API_KEY by lazy {
        getString(R.string.google_maps_key)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

//    private var isMapReady = false
//    private var hasLocationPermission = false
//    private var lastKnownLatLng: LatLng? = null

    private var currentMarkers = mutableListOf<com.google.android.gms.maps.model.Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel.nearbyPlaces.observe(viewLifecycleOwner) { places ->

            if (!::googleMap.isInitialized) return@observe

            currentMarkers.forEach { it.remove() }
            currentMarkers.clear()

            places.forEach { place ->
                place.latLng?.let { latLng ->
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(place.name ?: "Unknown place")
                    )

                    marker?.tag = place.id ?: ""
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

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(it, 15f)
                )
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

                 marker?.tag = details.id

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                )
            }

            Toast.makeText(requireContext(), details.name, Toast.LENGTH_SHORT).show()
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
            fetchLocationAndLoadPlaces() // onLocationReady()
        }

        googleMap.setOnMarkerClickListener { marker ->
            val placeId = marker.tag as? String
            if (placeId.isNullOrEmpty()) return@setOnMarkerClickListener true
            val name = marker.title ?: "Restaurant"

            val intent = Intent(requireContext(), RestaurantDetailActivity::class.java).apply {
                putExtra("placeId", placeId)
                putExtra("name", name)
            }

            startActivity(intent)
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
            fetchLocationAndLoadPlaces() // onLocationReady()
        } else {
            Toast.makeText(requireContext(),
                "Location permission required to load places",
                Toast.LENGTH_SHORT).show()
        }
    }

//    private fun loadPlaces(latLng: LatLng) {
//        viewModel.loadPlaces(API_KEY, latLng)
//    }

    private fun fetchLocationAndLoadPlaces() {
        getUserLocation { latLng ->

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, 14f)
            )

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