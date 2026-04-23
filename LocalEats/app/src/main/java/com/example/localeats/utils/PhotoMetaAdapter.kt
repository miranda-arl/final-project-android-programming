package com.example.localeats.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localeats.databinding.ImageRowBinding
import com.example.localeats.model.PhotoMeta
import com.example.localeats.ui.RestaurantDetailFragmentDirections


class PhotoMetaAdapter(private val viewModel: ReviewViewModel,
    private val onClick: (PhotoMeta) -> Unit)
    : ListAdapter<PhotoMeta, PhotoMetaAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<PhotoMeta>() {
        override fun areItemsTheSame(oldItem: PhotoMeta, newItem: PhotoMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: PhotoMeta, newItem: PhotoMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.pictureTitle == newItem.pictureTitle
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.ownerName == newItem.ownerName
                    && oldItem.uuid == newItem.uuid
                    && oldItem.byteSize == newItem.byteSize
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }

    inner class VH(private val rowBinding: ImageRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(photoMeta: PhotoMeta) {

            viewModel.glideFetch(photoMeta.uuid, rowBinding.rowImageView)

//            rowBinding.rowPictureTitle.text = photoMeta.pictureTitle
//            rowBinding.rowSize.text = photoMeta.byteSize.toString()
//            rowBinding.rowDate.text = photoMeta.timeStamp?.toDate().toString()

            itemView.setOnClickListener {

                val action =
                    RestaurantDetailFragmentDirections
                        .actionRestaurantDetailFragmentToPhotoDetailFragment(photoMeta)

                itemView.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = ImageRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}