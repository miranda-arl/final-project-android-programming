package com.example.localeats.utils

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.localeats.data.Review
import com.example.localeats.data.ReviewRepository
import com.example.localeats.glide.Glide
import com.example.localeats.model.PhotoMeta
import com.example.localeats.model.UploadState
import java.io.File

class ReviewViewModel : ViewModel() {
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    private val _photoMetaList = MutableLiveData<List<PhotoMeta>>()
    val photoMetaList: LiveData<List<PhotoMeta>> = _photoMetaList

    private var allReviews = listOf<Review>()

    private var pictureUUID = ""

    val rating: Float = 0f

    val averageRating = MutableLiveData<Float>()

    // Only call this from TakePictureWrapper
    fun takePictureUUID(uuid: String) {
        pictureUUID = uuid
    }

    var pictureNameByUser = "" // String provided by the user

    private val _uploadState = MutableLiveData<UploadState>(UploadState.Idle)
    val uploadState: LiveData<UploadState> = _uploadState

    var uploadedPhotoUUID: String? = null

    private var currentAuthUser = invalidUser
    private val repository = ReviewRepository()
    private val storage = Storage()

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
        repository.getReviewsForPlace(placeId) { list ->

            _reviews.postValue(list)

            val avg = if (list.isNotEmpty()) {
                list.map { it.rating }.average().toFloat()
            } else {
                0f
            }

            averageRating.postValue(avg)
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

    fun getPhotosForPlace(placeId: String) {
        repository.getReviewsForPlace(placeId) { reviews ->

            val photos = reviews
                .filter { it.imageUUID.isNotEmpty() }
                .map {
                    PhotoMeta(
                        firestoreID = it.firestoreID,
                        pictureTitle = it.comment,
                        ownerUid = it.userId,
                        ownerName = it.userName,
                        uuid = it.imageUUID,
                        byteSize = 0L,
                        timeStamp = it.timeStamp
                    )
                }

            _photoMetaList.postValue(photos)
        }
    }

    private fun createPhotoMeta(pictureTitle: String, uuid : String,
                                byteSize : Long) {
        val currentUser = currentAuthUser
        val photoMeta = PhotoMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            uuid = uuid,
            byteSize = byteSize,
            pictureTitle = pictureTitle,
        )
        repository.createPhotoMeta(photoMeta) {
            _photoMetaList.postValue(it)
        }
    }

    fun pictureSuccess() {
        val photoFile = TakePictureWrapper.fileNameToFile(pictureUUID)
        // XXX Write me while preserving referential integrity
        storage.uploadImage(photoFile, pictureUUID) { sizeBytes ->
            // This runs AFTER upload completes
            createPhotoMeta(
                pictureNameByUser,
                pictureUUID,
                sizeBytes
            )
            uploadedPhotoUUID = pictureUUID

            pictureUUID = ""
            pictureNameByUser = ""

            _uploadState.postValue(UploadState.Success)
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }

//    fun getCurrentPhotoUUID(): String {
//        return pictureUUID
//    }

    fun pictureFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        pictureUUID = ""
        pictureNameByUser = ""

        _uploadState.value = UploadState.Error("Upload failed")
    }

    fun glideFetch(uuid: String, imageView: ImageView) {
        Glide.fetch(storage.uuid2StorageReference(uuid),
            imageView)
    }
}