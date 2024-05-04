package com.noemi.imageloader.network

import com.noemi.imageloader.model.ZipoImage
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface ZipoAPI {

    @GET("image_list.json")
    fun loadImages(): Single<List<ZipoImage>>
}