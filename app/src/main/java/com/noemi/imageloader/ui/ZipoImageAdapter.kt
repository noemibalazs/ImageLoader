package com.noemi.imageloader.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noemi.imagecache.ImageLoader
import com.noemi.imageloader.databinding.ItemImageBinding
import com.noemi.imageloader.model.ZipoImage

class ZipoImageAdapter(
    images: List<ZipoImage>,
    private val loader: ImageLoader
) : RecyclerView.Adapter<ZipoImageAdapter.ImageViewHolder>() {

    private var zipos: List<ZipoImage> = images

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding: ItemImageBinding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val lastIndex = position == zipos.lastIndex
        holder.bindImage(zipos[position], lastIndex)
    }

    override fun getItemCount(): Int = zipos.size

    inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindImage(data: ZipoImage, lastIndex: Boolean) {

            with(binding) {
                imageId.text = data.id.toString()
                loader.execute(data.imageUrl, lastIndex, imageView)
            }
        }
    }

    fun updateImages(list: List<ZipoImage>) {
        zipos = list
        notifyDataSetChanged()
    }
}