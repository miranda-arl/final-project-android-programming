package com.example.localeats.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.example.localeats.R
import com.example.localeats.data.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddReviewActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private lateinit var placeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        placeId = intent.getStringExtra("placeId") ?: ""

        val uploadBtn = findViewById<Button>(R.id.uploadBtn)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        submitBtn.setOnClickListener {
            uploadReview()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data
        }
    }

    private fun uploadReview() {
        val rating = findViewById<RatingBar>(R.id.ratingBar).rating
        val comment = findViewById<EditText>(R.id.commentInput).text.toString()

        if (imageUri != null) {
            uploadImageAndSave(rating, comment)
        } else {
            saveReview(rating, comment, "")
        }
    }

    private fun uploadImageAndSave(rating: Float, comment: String) {
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("images/${UUID.randomUUID()}")

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .continueWithTask { task ->
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { downloadUrl ->
                    saveReview(rating, comment, downloadUrl.toString())
                }
        }
    }

    private fun saveReview(rating: Float, comment: String, imageUrl: String) {
        val user = FirebaseAuth.getInstance().currentUser

        val review = Review(
            userId = user?.uid ?: "",
            userName = user?.email ?: "Anonymous",
            comment = comment,
            rating = rating,
            placeId = placeId,
            imageUrl = imageUrl
        )

        FirebaseFirestore.getInstance()
            .collection("reviews")
            .add(review)
            .addOnSuccessListener {
                finish()
            }
    }
}