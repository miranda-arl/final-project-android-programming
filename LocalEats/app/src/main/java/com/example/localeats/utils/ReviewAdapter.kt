package com.example.localeats.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.R
import com.example.localeats.data.Review

class ReviewAdapter(private var reviews: List<Review>,
                    private val viewModel: ReviewViewModel) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val reviewText: TextView = view.findViewById(R.id.comment)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)

        val imageView: ImageView = view.findViewById(R.id.reviewImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = reviews.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        holder.userName.text = review.userName
        holder.reviewText.text = review.comment
        holder.ratingBar.rating = review.rating

        if (!review.imageUUID.isNullOrEmpty()) {
            holder.imageView.visibility = View.VISIBLE

            viewModel.glideFetch(review.imageUUID, holder.imageView)
        } else {
            holder.imageView.visibility = View.GONE
        }
    }

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}