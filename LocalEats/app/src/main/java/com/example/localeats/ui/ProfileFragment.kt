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
        adapter = ReviewAdapter(emptyList())
        recycler.adapter = adapter

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        user?.uid?.let {
            viewModel.getReviewsForUser(it)
        }

        viewModel.reviews.observe(viewLifecycleOwner) {
            adapter.updateData(it)
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