package com.example.localeats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.localeats.R
import com.example.localeats.data.Review
import com.example.localeats.model.UploadState
import com.example.localeats.utils.ReviewViewModel
import com.example.localeats.utils.TakePictureWrapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddReviewFragment : Fragment() {

    private val viewModel: ReviewViewModel by activityViewModels()

    private val args: AddReviewFragmentArgs by navArgs()

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.pictureSuccess()
        else viewModel.pictureFailure()
    }

    private lateinit var previewImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        submitBtn.isEnabled = false
        viewModel.uploadedPhotoUUID = null
        viewModel.resetUploadState()

        uploadBtn.setOnClickListener {
            viewModel.pictureNameByUser = args.placeId
            TakePictureWrapper.takePicture(
                args.placeId,
                requireActivity(),
                viewModel,
                cameraLauncher
            )
        }
        previewImage = view.findViewById<ImageView>(R.id.previewImage)

        submitBtn.setOnClickListener {
            val rating = ratingBar.rating
            val comment = commentInput.text.toString()
            val imageUUID = viewModel.uploadedPhotoUUID ?: ""

            saveReview(rating, comment, imageUUID)
        }

        previewImage.visibility = View.GONE
        // enable submit when upload succeeds
        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is UploadState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    submitBtn.isEnabled = false
                    uploadBtn.isEnabled = false
                }

                is UploadState.Success -> {
                    progressBar.visibility = View.GONE
                    submitBtn.isEnabled = true
                    uploadBtn.isEnabled = false

                    val uuid = viewModel.uploadedPhotoUUID

                    if (uuid != null) {
                        previewImage.visibility = View.VISIBLE
                        viewModel.glideFetch(uuid, previewImage)
                    }

                    Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                }

                is UploadState.Error -> {
                    progressBar.visibility = View.GONE
                    submitBtn.isEnabled = false
                    uploadBtn.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }

    private fun saveReview(rating: Float, comment: String, imageUUID: String) {
        val user = FirebaseAuth.getInstance().currentUser

        val review = Review(
            userId = user?.uid ?: "",
            userName = user?.email ?: "Anonymous",
            comment = comment,
            rating = rating,
            placeId = args.placeId,
            placeName = args.name,
            placeAddress = args.address,
            imageUUID = imageUUID
        )

        FirebaseFirestore.getInstance()
            .collection("reviews")
            .add(review)
            .addOnSuccessListener {
                viewModel.uploadedPhotoUUID = null
                viewModel.resetUploadState()
                findNavController().popBackStack()
            }
    }
}