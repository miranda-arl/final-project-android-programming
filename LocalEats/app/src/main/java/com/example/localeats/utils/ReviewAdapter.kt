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
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.graphics.Color

class ReviewAdapter(private var reviews: List<Review>,
                    private val viewModel: ReviewViewModel,
                    private val showPlaceInfo: Boolean = false) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    private var query: String = ""

    fun setQuery(q: String) {
        query = q
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
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
        holder.userName.text = highlightQuery(review.userName, query)
        holder.reviewText.text = highlightQuery(review.comment, query)

        if (showPlaceInfo) {
            holder.placeName.visibility = View.VISIBLE
            holder.placeAddress.visibility = View.VISIBLE

            holder.placeName.text = review.placeName
            holder.placeAddress.text = review.placeAddress
        } else {
            holder.placeName.visibility = View.GONE
            holder.placeAddress.visibility = View.GONE
        }

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

    fun highlightQuery(text: String, query: String): SpannableString {
        val spannable = SpannableString(text)
        val q = query.trim()

        if (q.isEmpty()) return spannable

        val lowerText = text.lowercase()
        val lowerQuery = q.lowercase()

        var startIndex = lowerText.indexOf(lowerQuery)
        while (startIndex >= 0) {
            spannable.setSpan(
                BackgroundColorSpan(Color.YELLOW),
                startIndex,
                startIndex + q.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            startIndex = lowerText.indexOf(lowerQuery, startIndex + q.length)
        }
        return spannable
    }
}