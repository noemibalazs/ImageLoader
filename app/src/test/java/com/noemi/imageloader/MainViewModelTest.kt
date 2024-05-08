package com.noemi.imageloader

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.remotedatasource.ZipoImageDataSource
import com.noemi.imageloader.ui.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private val dispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var zipoDataSource: ZipoImageDataSource

    private lateinit var viewModel: MainViewModel

    private val image: ZipoImage = mock()
    private val error: Exception = mock()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcher)
        viewModel = MainViewModel(zipoDataSource)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `test load images and should be successful`() = runBlocking {
        val job = launch {
            assertThat(viewModel.viewState.value).isEqualTo(MainViewModel.ViewState.Loading)

            zipoDataSource.loadImages().test {
                val result = awaitItem()
                assertThat(viewModel.viewState.value).isEqualTo(MainViewModel.ViewState.Loaded)
                assertThat(result).isEqualTo(listOf(image))

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.loadImages()

        job.cancelAndJoin()
    }

    @Test
    fun `test load images and should throws exception`() = runBlocking {
        val job = launch {
            assertThat(viewModel.viewState.value).isEqualTo(MainViewModel.ViewState.Loading)

            zipoDataSource.loadImages().test {
                val result = awaitError()
                assertThat(viewModel.viewState.value).isEqualTo(MainViewModel.ViewState.Failed)
                assertThat(result).isEqualTo(error)

                cancelAndIgnoreRemainingEvents()
            }
        }

        viewModel.loadImages()

        job.cancelAndJoin()
    }
}