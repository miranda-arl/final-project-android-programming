package com.example.localeats.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localeats.data.Review
import com.example.localeats.data.ReviewRepository

class ReviewViewModel : ViewModel() {

    private val repository = ReviewRepository()

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    private var allReviews = listOf<Review>()

    fun startListening() {
        repository.getReviews { list ->
            allReviews = list
            _reviews.value = list
        }
    }

    fun addReview(review: Review) {
        repository.addReview(review)
    }

    fun getReviewsForPlace(placeId: String) {
        repository.getReviewsForPlace(placeId) {
            _reviews.postValue(it)
        }
    }

    fun getReviewsForUser(userId: String) {
        repository.getReviewsForUser(userId) {
            _reviews.postValue(it)
        }
    }

    fun filterReviews(query: String) {
        if (query.isBlank()) {
            _reviews.value = allReviews
            return
        }

        val filtered = allReviews.filter { review ->
            review.comment.contains(query, ignoreCase = true) ||
                    review.userName.contains(query, ignoreCase = true) ||
                    review.placeId.contains(query, ignoreCase = true)
        }

        _reviews.value = filtered
    }
}