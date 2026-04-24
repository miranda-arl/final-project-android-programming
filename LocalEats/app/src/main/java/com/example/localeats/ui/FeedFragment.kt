package com.example.localeats.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.R
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
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.queryHint = "Search places or reviews to filter"

        val params = searchView.layoutParams

        val label = view.findViewById<TextView>(R.id.searchLabel)

        searchView.setOnSearchClickListener {
            label.visibility = View.GONE
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            searchView.layoutParams = params
        }
        searchView.setOnCloseListener {
            label.visibility = View.VISIBLE
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            searchView.layoutParams = params
            false
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        adapter = ReviewAdapter(emptyList(), viewModel, showPlaceInfo = true)
        recyclerView.adapter = adapter

        // Observe filtered reviews
        viewModel.reviews.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        // Start Firestore listener
        viewModel.startListening()

        // Search logic + highlighting trigger
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText ?: ""

                // filter data
                viewModel.filterReviews(q)

                // pass query to adapter for highlighting
                adapter.setQuery(q)

                return true
            }
        })

        return view
    }
}