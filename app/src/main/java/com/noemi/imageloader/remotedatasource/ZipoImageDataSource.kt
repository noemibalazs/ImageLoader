package com.noemi.imageloader.remotedatasource

import com.noemi.imageloader.model.ZipoImage
import kotlinx.coroutines.flow.Flow

interface ZipoImageDataSource {

    fun loadImages(): Flow<List<ZipoImage>>
}