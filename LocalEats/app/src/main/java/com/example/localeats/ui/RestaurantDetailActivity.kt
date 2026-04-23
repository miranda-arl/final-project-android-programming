package com.example.localeats.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.R
import com.example.localeats.utils.PhotoMetaAdapter
import com.example.localeats.utils.ReviewAdapter
import com.example.localeats.utils.ReviewViewModel

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var imageAdapter: PhotoMetaAdapter

    private lateinit var placeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        placeId = intent.getStringExtra("placeId") ?: ""
        val name = intent.getStringExtra("name")

        findViewById<TextView>(R.id.restaurantName).text = name
        findViewById<TextView>(R.id.address).text = intent.getStringExtra("address")

        val recycler = findViewById<RecyclerView>(R.id.reviewRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        reviewAdapter = ReviewAdapter(emptyList(), viewModel)
        recycler.adapter = reviewAdapter

        viewModel.getReviewsForPlace(placeId)

        viewModel.reviews.observe(this) {
            reviewAdapter.updateData(it)
        }

        val photoRecycler = findViewById<RecyclerView>(R.id.photosRv)
        photoRecycler.layoutManager = LinearLayoutManager(this)

        imageAdapter = PhotoMetaAdapter(viewModel) { photo ->
            val fragment = PhotoDetailFragment.newInstance(photo)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        photoRecycler.adapter = imageAdapter

        viewModel.getPhotosForPlace(placeId)

        viewModel.photoMetaList.observe(this) {
            imageAdapter.submitList(it)
        }

        findViewById<Button>(R.id.addReviewBtn).setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra("placeId", placeId)
            startActivity(intent)
        }
    }
}