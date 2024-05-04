package com.noemi.imageloader.ui

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noemi.imagecache.PeriodicClearCacheListener
import com.noemi.imagecache.ImageDiskCache
import com.noemi.imagecache.ImageMemoryCache
import com.noemi.imagecache.ImageLoader
import com.noemi.imageloader.databinding.ItemImageBinding
import com.noemi.imageloader.model.ZipoImage

class ZipoImageAdapter(
    private val imageCache: ImageMemoryCache,
    private val diskCache: ImageDiskCache,
    private val placeHolder: Bitmap,
    images: List<ZipoImage>,
    private val listener: PeriodicClearCacheListener
) : RecyclerView.Adapter<ZipoImageAdapter.ImageViewHolder>() {

    private var zipos: List<ZipoImage> = images

    init {
        imageCache.initializeCache()
    }

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
                ImageLoader(imageCache, diskCache, imageView, placeHolder, listener).execute(data.imageUrl, lastIndex)
            }
        }
    }

    fun updateImages(list: List<ZipoImage>) {
        zipos = list
        notifyDataSetChanged()
    }
}