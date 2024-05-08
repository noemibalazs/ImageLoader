package com.noemi.imageloader

import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.network.ZipoAPI
import com.noemi.imageloader.remotedatasource.ZipoImageDataSource
import com.noemi.imageloader.remotedatasource.ZipoImageDataSourceImpl
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ZipoImageDataSourceImplTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var remoteDataSource: ZipoImageDataSource

    @Mock
    private lateinit var zipoAPI: ZipoAPI

    private val result: ZipoImage = mock()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        remoteDataSource = ZipoImageDataSourceImpl(zipoAPI, dispatcher)
    }

    @Test
    fun `test get remote images and should be successful`() = runBlocking {

        val job = launch {
            val response = zipoAPI.loadImagesAsFlow()
            response.shouldBe(listOf(result))
        }

        remoteDataSource.loadImages()

        job.cancelAndJoin()
    }


    @Test
    fun `test get remote images and should be empty`() = runBlocking {

        val job = launch {
            val response = zipoAPI.loadImagesAsFlow()
            response.shouldBe(emptyList())
        }

        remoteDataSource.loadImages()

        job.cancelAndJoin()
    }
}