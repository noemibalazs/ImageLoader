package com.noemi.imageloader

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.remotedatasource.ZipoImageDataSource
import com.noemi.imageloader.ui.MainViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mockKRule = MockKRule(this)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = RxJavaSchedulerTrampolineRule()

    @MockK
    private lateinit var zipoDataSource: ZipoImageDataSource

    @MockK
    private lateinit var viewStateObserver: Observer<MainViewModel.ViewState>

    @MockK
    private lateinit var imagesObserver: Observer<List<ZipoImage>>

    private val imagesCaptor = mutableListOf<List<ZipoImage>>()
    private val stateCaptor = mutableListOf<MainViewModel.ViewState>()

    private val testScheduler = TestScheduler()
    private lateinit var viewModel: MainViewModel

    private val image: ZipoImage = mockk()
    private val error: Exception = mockk()

    @Before
    fun setUp() {
        viewModel = MainViewModel(zipoDataSource)

        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        imagesCaptor.clear()
        stateCaptor.clear()

        every { imagesObserver.onChanged(capture(imagesCaptor)) } just runs
        every { viewStateObserver.onChanged(capture(stateCaptor)) } just runs

        viewModel.viewState.observeForever(viewStateObserver)
        viewModel.zipoImages.observeForever(imagesObserver)
    }

    @Test
    fun `test load images and should be successful`() {
        every { zipoDataSource.loadImages() } returns Single.just(listOf(image))

        viewModel.loadImages()

        verify(exactly = 1) { zipoDataSource.loadImages() }
        verify(exactly = 1) { viewStateObserver.onChanged(MainViewModel.ViewState.Loading) }
        verify(exactly = 1) { imagesObserver.onChanged(listOf(image)) }
        verify(exactly = 1) { viewStateObserver.onChanged(MainViewModel.ViewState.Loaded) }
    }

    @Test
    fun `test load images and should throws exception`() {
        every { zipoDataSource.loadImages() } returns Single.error(error)

        viewModel.loadImages()

        verify(exactly = 1) { zipoDataSource.loadImages() }
        verify(exactly = 1) { viewStateObserver.onChanged(MainViewModel.ViewState.Loading) }
        verify(exactly = 0) { imagesObserver.onChanged(listOf(image)) }
        verify(exactly = 1) { viewStateObserver.onChanged(MainViewModel.ViewState.Failed) }
    }
}