package com.example.localeats.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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
        val address = arguments?.getString("address")

        view.findViewById<TextView>(R.id.restaurantName).text = name
        view.findViewById<TextView>(R.id.address).text = address

        val lcnBtn = view.findViewById<ImageButton>(R.id.locationBtn)
        setBackgroundDrawable(lcnBtn, R.drawable.baseline_location_on_24)

        val reviewRecycler = view.findViewById<RecyclerView>(R.id.reviewRecycler)
        reviewRecycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(requireActivity())[ReviewViewModel::class.java]

        reviewAdapter = ReviewAdapter(emptyList(), viewModel)
        reviewRecycler.adapter = reviewAdapter

        viewModel.getReviewsForPlace(placeId)

        viewModel.reviews.observe(viewLifecycleOwner) {
            reviewAdapter.updateData(it)
        }

        val photoRecycler = view.findViewById<RecyclerView>(R.id.photosRv)
        photoRecycler.layoutManager = GridLayoutManager(requireContext(), 3)

        imageAdapter = PhotoMetaAdapter(viewModel) { photo ->
            val action =
                RestaurantDetailFragmentDirections
                    .actionRestaurantDetailFragmentToPhotoDetailFragment(photo)

            findNavController().navigate(action)
        }

        photoRecycler.adapter = imageAdapter

        // 2. observe FIRST
        viewModel.photoMetaList.observe(viewLifecycleOwner) {
            imageAdapter.submitList(it) // updateData(it)
            // imageAdapter.notifyDataSetChanged()
        }

        // 3. THEN trigger load
        viewModel.getPhotosForPlace(placeId)

        view.findViewById<Button>(R.id.addReviewBtn).setOnClickListener {
            val action =
                RestaurantDetailFragmentDirections
                    .actionRestaurantDetailFragmentToAddReviewFragment(placeId)

            findNavController().navigate(action)
        }

        viewModel.averageRating.observe(viewLifecycleOwner) { avg ->
            view.findViewById<RatingBar>(R.id.ratingBar).rating = avg

            view.findViewById<TextView>(R.id.ratingText).text =
                "Rating: ${avg ?: 0.0} (${viewModel.reviews.value?.size ?: 0} reviews)"
        }

        return view
    }

    fun setBackgroundDrawable(button: ImageButton, resourceId: Int) {
        button.setBackgroundResource(resourceId)
        button.tag = resourceId
    }
}