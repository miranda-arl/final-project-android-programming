package com.example.localeats.data

import com.google.firebase.firestore.FirebaseFirestore

class ReviewRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getReviews(onUpdate: (List<Review>) -> Unit) {
        db.collection("reviews")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val reviews = snapshot
                    ?.toObjects(Review::class.java)
                    ?: emptyList()

                onUpdate(reviews)
            }
    }

    fun addReview(review: Review) {
        db.collection("reviews")
            .add(review)
    }

    fun getReviewsForPlace(
        placeId: String,
        onUpdate: (List<Review>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("placeId", placeId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val reviews = snapshot
                    ?.toObjects(Review::class.java)
                    ?: emptyList()

                onUpdate(reviews)
            }
    }

    fun getReviewsForUser(
        userId: String,
        onUpdate: (List<Review>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val reviews = snapshot
                    ?.toObjects(Review::class.java)
                    ?: emptyList()

                onUpdate(reviews)
            }
    }
}