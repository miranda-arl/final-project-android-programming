package com.example.localeats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.R
import com.example.localeats.data.Review
import com.example.localeats.utils.ReviewAdapter
import com.example.localeats.utils.ReviewViewModel

class FeedFragment : Fragment() {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.feedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ReviewAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        viewModel.reviews.observe(viewLifecycleOwner) {
            android.util.Log.d("FEED", "Received reviews: ${it.size}")
            adapter.updateData(it)
        }

        viewModel.startListening()

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterReviews(newText ?: "")
                return true
            }
        })

        return view
    }
}