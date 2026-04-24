package com.example.localeats.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.R
import com.example.localeats.utils.LoginActivity
import com.example.localeats.utils.ReviewAdapter
import com.example.localeats.utils.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val user = FirebaseAuth.getInstance().currentUser

        val emailText = view.findViewById<TextView>(R.id.userEmail)
        val logoutBtn = view.findViewById<Button>(R.id.logoutBtn)
        val recycler = view.findViewById<RecyclerView>(R.id.myReviewsRecycler)

        emailText.text = user?.email ?: "Not logged in"

        recycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(requireActivity())[ReviewViewModel::class.java]

        adapter = ReviewAdapter(emptyList(), viewModel, showPlaceInfo = true)
        recycler.adapter = adapter

        user?.uid?.let {
            viewModel.getReviewsForUser(it)
        }

        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            adapter.updateData(reviews)

            val emptyView = view.findViewById<TextView>(R.id.emptyMyReviewsText)

            if (reviews.isNullOrEmpty()) {
                recycler.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recycler.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}