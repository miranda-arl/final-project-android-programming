package com.example.localeats.data

import android.util.Log
import com.example.localeats.model.PhotoMeta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ReviewRepository {

    private val db = FirebaseFirestore.getInstance()
    private val rootCollection = "reviews"

    fun getReviews(onUpdate: (List<Review>) -> Unit) {
        db.collection(rootCollection)
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
        db.collection(rootCollection)
            .add(review)
    }

    fun getReviewsForPlace(
        placeId: String,
        onUpdate: (List<Review>) -> Unit) {
        db.collection(rootCollection)
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
        db.collection(rootCollection)
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

    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    private fun limitAndGet(query: Query,
                            resultListener: (List<PhotoMeta>)->Unit) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "reviews fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(PhotoMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "reviews fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    fun fetchPhotoMeta(
        resultListener: (List<PhotoMeta>) -> Unit
    ) {
        val field = "timestamp"
        val direction = Query.Direction.DESCENDING

        limitAndGet(
            db.collection(rootCollection).orderBy(field, direction),
            resultListener
        )
    }

    fun getPhotoMetaForPlace(placeId: String, callback: (List<PhotoMeta>) -> Unit) {
        db.collection(rootCollection)
            .whereEqualTo("placeId", placeId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { it.toObject(PhotoMeta::class.java) }
                callback(list)
            }
    }

    fun removePhotoMeta(
        // sortInfo: SortInfo,
        photoMeta: PhotoMeta,
        resultListener: (List<PhotoMeta>)->Unit
    ) {
        // XXX Write me.  Make sure you delete the correct entry.  What uniquely identifies a photoMeta?
        db.collection(rootCollection).whereEqualTo("uuid", photoMeta.uuid)
            .get()
            .addOnSuccessListener { result ->
                if(result.documents.size == 0) {
                    Log.d(javaClass.simpleName, "removePhotoMeta FAILED no match ${photoMeta.uuid}")
                    fetchPhotoMeta(resultListener)
                } else {
                    db.collection(rootCollection).document(result.documents[0].id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(javaClass.simpleName, "removePhotoMeta succeeded ${photoMeta.uuid}")
                            fetchPhotoMeta(resultListener)
                        }
                        .addOnFailureListener {
                            Log.d(javaClass.simpleName, "removePhotoMeta FAILED ${photoMeta.uuid}", it)
                            fetchPhotoMeta(resultListener)
                        }
                }
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "removePhotoMeta FAILED ${photoMeta.uuid}", it)
                fetchPhotoMeta(resultListener)
            }
    }
}