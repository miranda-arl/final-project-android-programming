package com.example.localeats.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.R
import com.example.localeats.utils.MapViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places

class SearchFragment : Fragment() {

    private lateinit var viewModel: MapViewModel
    private lateinit var adapter: PlacesAdapter

    private val API_KEY by lazy {
        getString(R.string.google_maps_key)
    }

    private val placesClient by lazy {
        Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]

        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val clearBut = view.findViewById<View>(R.id.clear_button)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PlacesAdapter { place ->
            place.id?.let { viewModel.loadPlaceDetails(API_KEY, it) }
        }

        recyclerView.adapter = adapter

        viewModel.searchResults.observe(viewLifecycleOwner) { places ->
            adapter.submitList(places)
        }

        clearBut.setOnClickListener {
            searchInput.text.clear()
        }

        searchInput.addTextChangedListener {
            val query = it.toString().trim()
            if (query.length >= 2) {
                Log.e("SEARCH FRAGMENT", "Searching for: $query")
                viewModel.searchPlaces(API_KEY, query)
                recyclerView.visibility = View.VISIBLE
            }
            if (query.isEmpty()) {
                // optional manual reset trigger
                viewModel.loadPlaces(API_KEY, LatLng(37.7749, -122.4194)) // fix it! TODO
                recyclerView.visibility = View.GONE
            }
        }
    }
}