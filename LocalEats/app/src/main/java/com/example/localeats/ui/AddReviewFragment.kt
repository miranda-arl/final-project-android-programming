package com.example.localeats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.localeats.R
import com.example.localeats.data.Review
import com.example.localeats.utils.ReviewViewModel
import com.example.localeats.utils.TakePictureWrapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddReviewFragment : Fragment() {

    private val viewModel: ReviewViewModel by activityViewModels()

    private var placeId: String = ""

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.pictureSuccess()
        else viewModel.pictureFailure()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = requireArguments()
        placeId = args.getString("placeId") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val uploadBtn = view.findViewById<Button>(R.id.uploadBtn)
        val submitBtn = view.findViewById<Button>(R.id.submitBtn)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val commentInput = view.findViewById<EditText>(R.id.commentInput)

        submitBtn.isEnabled = false

        uploadBtn.setOnClickListener {
            viewModel.pictureNameByUser = placeId
            TakePictureWrapper.takePicture(
                placeId,
                requireActivity(),
                viewModel,
                cameraLauncher
            )
        }

        submitBtn.setOnClickListener {
            val rating = ratingBar.rating
            val comment = commentInput.text.toString()
            val imageUUID = viewModel.uploadedPhotoUUID ?: ""

            saveReview(rating, comment, imageUUID)
        }

        // enable submit when upload succeeds
//        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
//            submitBtn.isEnabled = state == UploadState.SUCCESS
//        }
    }

    private fun saveReview(rating: Float, comment: String, imageUUID: String) {
        val user = FirebaseAuth.getInstance().currentUser

        val review = Review(
            userId = user?.uid ?: "",
            userName = user?.email ?: "Anonymous",
            comment = comment,
            rating = rating,
            placeId = placeId,
            imageUUID = imageUUID
        )

        FirebaseFirestore.getInstance()
            .collection("reviews")
            .add(review)
            .addOnSuccessListener {
                findNavController().popBackStack()
            }
    }
}