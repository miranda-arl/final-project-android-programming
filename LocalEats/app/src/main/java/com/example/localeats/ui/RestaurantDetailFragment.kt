package com.example.localeats.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.R
import com.example.localeats.utils.PhotoMetaAdapter
import com.example.localeats.utils.ReviewAdapter
import com.example.localeats.utils.ReviewViewModel

class RestaurantDetailFragment : Fragment() {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var imageAdapter: PhotoMetaAdapter

    private var placeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placeId = arguments?.getString("placeId") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false)

        val name = arguments?.getString("name")
        val address = "("+arguments?.getString("address")+")"

        view.findViewById<TextView>(R.id.restaurantName).text = name
        view.findViewById<TextView>(R.id.address).text = address

        val reviewRecycler = view.findViewById<RecyclerView>(R.id.reviewRecycler)
        reviewRecycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        reviewAdapter = ReviewAdapter(emptyList(), viewModel)
        reviewRecycler.adapter = reviewAdapter

        viewModel.getReviewsForPlace(placeId)

        viewModel.reviews.observe(viewLifecycleOwner) {
            reviewAdapter.updateData(it)
        }

        val photoRecycler = view.findViewById<RecyclerView>(R.id.photosRv)
        photoRecycler.layoutManager = LinearLayoutManager(requireContext())

        imageAdapter = PhotoMetaAdapter(viewModel) { photo ->
            val action =
                RestaurantDetailFragmentDirections
                    .actionRestaurantDetailFragmentToPhotoDetailFragment(photo)

            findNavController().navigate(action)
        }

        photoRecycler.adapter = imageAdapter

        viewModel.getPhotosForPlace(placeId)

        view.findViewById<Button>(R.id.addReviewBtn).setOnClickListener {
            val action =
                RestaurantDetailFragmentDirections
                    .actionRestaurantDetailFragmentToAddReviewFragment(placeId)

            findNavController().navigate(action)
        }

        return view
    }
}