package com.example.localeats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
    private var name: String = ""
    private var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placeId = arguments?.getString("placeId") ?: ""
        name = arguments?.getString("name") ?: ""
        address = arguments?.getString("address") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false)

        view.findViewById<TextView>(R.id.restaurantName).text = name
        view.findViewById<TextView>(R.id.address).text = address

        val lcnBtn = view.findViewById<ImageButton>(R.id.locationBtn)
        setBackgroundDrawable(lcnBtn, R.drawable.baseline_location_on_24)

        val emptyPhotosView = view.findViewById<TextView>(R.id.emptyPhotos)
        val emptyReviewsView = view.findViewById<TextView>(R.id.emptyReviews)

        val reviewRecycler = view.findViewById<RecyclerView>(R.id.reviewRecycler)
        reviewRecycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel = ViewModelProvider(requireActivity())[ReviewViewModel::class.java]

        reviewAdapter = ReviewAdapter(emptyList(), viewModel, showPlaceInfo = false)
        reviewRecycler.adapter = reviewAdapter

        viewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            reviewAdapter.updateData(reviews)

            if (reviews.isNullOrEmpty()) {
                emptyReviewsView.visibility = View.VISIBLE
                reviewRecycler.visibility = View.GONE
            } else {
                emptyReviewsView.visibility = View.GONE
                reviewRecycler.visibility = View.VISIBLE
            }
        }

        viewModel.getReviewsForPlace(placeId)

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
        viewModel.photoMetaList.observe(viewLifecycleOwner) { photos ->
            imageAdapter.submitList(photos)

            if (photos.isNullOrEmpty()) {
                emptyPhotosView.visibility = View.VISIBLE
                photoRecycler.visibility = View.GONE
            } else {
                emptyPhotosView.visibility = View.GONE
                photoRecycler.visibility = View.VISIBLE
            }
        }

        // 3. THEN trigger load
        viewModel.getPhotosForPlace(placeId)

        view.findViewById<Button>(R.id.addReviewBtn).setOnClickListener {
            val action =
                RestaurantDetailFragmentDirections
                    .actionRestaurantDetailFragmentToAddReviewFragment(placeId, name, address)

            findNavController().navigate(action)
        }

        viewModel.averageRating.observe(viewLifecycleOwner) { avg ->
            view.findViewById<RatingBar>(R.id.ratingBar).rating = avg

            val reviewCount = viewModel.reviews.value?.size ?: 0

            view.findViewById<TextView>(R.id.ratingText).text =
                "Rating: $avg ($reviewCount reviews)"
        }

        return view
    }

    fun setBackgroundDrawable(button: ImageButton, resourceId: Int) {
        button.setBackgroundResource(resourceId)
        button.tag = resourceId
    }
}