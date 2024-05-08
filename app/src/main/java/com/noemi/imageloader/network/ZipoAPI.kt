package com.noemi.imageloader.network

import com.noemi.imageloader.model.ZipoImage
import retrofit2.http.GET

interface ZipoAPI {

    @GET("image_list.json")
    suspend fun loadImagesAsFlow(): List<ZipoImage>
}