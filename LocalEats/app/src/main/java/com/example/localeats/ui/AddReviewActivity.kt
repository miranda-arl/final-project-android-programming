//package com.example.localeats.ui
//
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.RatingBar
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import com.example.localeats.R
//import com.example.localeats.data.Review
//import com.example.localeats.utils.ReviewViewModel
//import com.example.localeats.utils.TakePictureWrapper
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class AddReviewActivity : AppCompatActivity() {
//
//    private var imageUUID: String? = null
//    private lateinit var placeId: String
//
//    private val viewModel: ReviewViewModel by viewModels()
//
//    private val cameraLauncher = registerForActivityResult(
//        ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            viewModel.pictureSuccess()
//        } else {
//            viewModel.pictureFailure()
//        }
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_add_review)
//
//        placeId = intent.getStringExtra("placeId") ?: ""
//
//        val uploadBtn = findViewById<Button>(R.id.uploadBtn)
//        val submitBtn = findViewById<Button>(R.id.submitBtn)
//
//        uploadBtn.setOnClickListener {
//            viewModel.pictureNameByUser = placeId
//            TakePictureWrapper.takePicture(
//                placeId,
//                this,
//                viewModel,
//                cameraLauncher
//            )
//        }
//
//        submitBtn.setOnClickListener {
//            uploadReview()
//        }
//    }
//
//    private fun uploadReview() {
//        val rating = findViewById<RatingBar>(R.id.ratingBar).rating
//        val comment = findViewById<EditText>(R.id.commentInput).text.toString()
//
//        val imageUUID = viewModel.uploadedPhotoUUID ?: ""
//        saveReview(rating, comment, imageUUID)
//    }
//
//    private fun saveReview(rating: Float, comment: String, imageUUID: String) {
//        val user = FirebaseAuth.getInstance().currentUser
//
//        val review = Review(
//            userId = user?.uid ?: "",
//            userName = user?.email ?: "Anonymous",
//            comment = comment,
//            rating = rating,
//            placeId = placeId,
//            imageUUID = imageUUID
//        )
//
//        FirebaseFirestore.getInstance()
//            .collection("reviews")
//            .add(review)
//            .addOnSuccessListener {
//                finish()
//            }
//    }
//}