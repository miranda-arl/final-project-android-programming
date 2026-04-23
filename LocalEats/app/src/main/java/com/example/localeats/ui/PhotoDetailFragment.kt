package com.example.localeats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.localeats.R
import com.example.localeats.utils.ReviewViewModel

class PhotoDetailFragment : Fragment() {

    private val args: PhotoDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_photo_detail, container, false)

        val photo = args.photo

        view.findViewById<TextView>(R.id.detailTitle).text = photo.pictureTitle
        view.findViewById<TextView>(R.id.detailUser).text = photo.ownerName
        view.findViewById<TextView>(R.id.detailDate).text =
            photo.timeStamp?.toDate()?.toString() ?: "Unknown"

        val imageView = view.findViewById<ImageView>(R.id.detailImage)
        (activity as ReviewViewModel).glideFetch(photo.uuid, imageView)

        return view
    }
}