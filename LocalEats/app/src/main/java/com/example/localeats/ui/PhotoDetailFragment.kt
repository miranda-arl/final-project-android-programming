package com.example.localeats.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.localeats.R
import com.example.localeats.utils.ReviewViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PhotoDetailFragment : Fragment() {

    private val args: PhotoDetailFragmentArgs by navArgs()
    private lateinit var viewModel: ReviewViewModel

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_photo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ReviewViewModel::class.java]

        val photo = args.photo
        val date = photo.timeStamp?.toDate()
        // Log.e("PhotoDetailFragment", "Photo timestamp: ${photo.timeStamp}, Date: $date")

        val formattedDate = date?.let { dateFormatter.format(it) } ?: ""

        view.findViewById<TextView>(R.id.detailDate).text = formattedDate

        view.findViewById<TextView>(R.id.detailTitle).text =
            photo.pictureTitle.ifBlank { "" }

        view.findViewById<TextView>(R.id.detailUser).text =
            photo.ownerName.ifBlank { "Anonymous" }

        val imageView = view.findViewById<ImageView>(R.id.detailImage)

        viewModel.glideFetch(photo.uuid, imageView)
    }
}