package com.noemi.imageloader.remotedatasource

import com.noemi.imageloader.model.ZipoImage
import io.reactivex.rxjava3.core.Single

interface ZipoImageDataSource {

    fun loadImages(): Single<List<ZipoImage>>
}