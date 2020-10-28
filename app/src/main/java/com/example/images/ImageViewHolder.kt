package com.example.images

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ImageViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    fun bind(image: Image) {
        with(root){
            image_description.text=image.description
            image_url.text=image.url
        }
    }
}