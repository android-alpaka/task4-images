package com.example.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(
        private val images: List<Image>,
        private val onClick: (Image) -> Unit
) : RecyclerView.Adapter<ImageViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ImageViewHolder {
        val holder = ImageViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item, parent, false)
        )
        holder.itemView.setOnClickListener {
            onClick(images[holder.adapterPosition])
        }
        return holder

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
            holder.bind(images[position])

    override fun getItemCount() = images.size
}