package com.example.localeats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.localeats.R
import com.example.localeats.model.PhotoMeta
import com.example.localeats.utils.ReviewViewModel

class PhotoDetailFragment : Fragment() {

    private lateinit var photo: PhotoMeta
    private val viewModel: ReviewViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photo = requireArguments().getParcelable("photo")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.one_image, container, false)

        val imageView = view.findViewById<ImageView>(R.id.detailImage)
        val title = view.findViewById<TextView>(R.id.detailTitle)
        val user = view.findViewById<TextView>(R.id.detailUser)
        val date = view.findViewById<TextView>(R.id.detailDate)

        title.text = photo.pictureTitle
        user.text = photo.ownerName
        date.text = photo.timeStamp?.toDate()?.toString() ?: "Unknown"

        viewModel.glideFetch(photo.uuid, imageView)

        return view
    }

    companion object {
        fun newInstance(photo: PhotoMeta): PhotoDetailFragment {
            return PhotoDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("photo", photo)
                }
            }
        }
    }
}