package com.noemi.imageloader.remotedatasource

import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.network.ZipoAPI
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ZipoImageDataSourceImpl @Inject constructor(private val zipoAPI: ZipoAPI) : ZipoImageDataSource {

    override fun loadImages(): Single<List<ZipoImage>> =
        Single.just(true)
            .flatMap { zipoAPI.loadImages() }
}
