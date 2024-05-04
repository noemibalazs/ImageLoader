package com.noemi.imageloader

import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.network.ZipoAPI
import com.noemi.imageloader.remotedatasource.ZipoImageDataSource
import com.noemi.imageloader.remotedatasource.ZipoImageDataSourceImpl
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ZipoImageDataSourceImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var remoteDataSource: ZipoImageDataSource

    @MockK
    private lateinit var zipoAPI: ZipoAPI

    private val result: ZipoImage = mockk()
    private val error: Exception = mockk()

    @Before
    fun setUp() {
        remoteDataSource = ZipoImageDataSourceImpl(zipoAPI)
    }

    @Test
    fun `test get remote images and should be successful`() {
        every { zipoAPI.loadImages() } returns Single.just(listOf(result))

        remoteDataSource.loadImages()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValue { it == listOf(result) }

        verify(exactly = 1) { zipoAPI.loadImages() }
    }

    @Test
    fun `test get remote images and throws exception`() {
        every { zipoAPI.loadImages() } returns Single.error(error)

        remoteDataSource.loadImages()
            .test()
            .assertNoValues()
            .assertNotComplete()
            .assertError(error)

        verify(exactly = 1) { zipoAPI.loadImages() }
    }
}