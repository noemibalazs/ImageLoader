package com.noemi.imageloader.remotedatasource

import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.network.ZipoAPI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ZipoImageDataSourceImpl @Inject constructor(
    private val zipoAPI: ZipoAPI,
    private val dispatcher: CoroutineDispatcher
) : ZipoImageDataSource {

    override fun loadImages(): Flow<List<ZipoImage>> =
        flow { emit(zipoAPI.loadImagesAsFlow()) }
            .flowOn(dispatcher)
}
