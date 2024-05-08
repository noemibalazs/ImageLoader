package com.noemi.imageloader.ui

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.noemi.imageloader.base.BaseViewModel
import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.remotedatasource.ZipoImageDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val imageDataSource: ZipoImageDataSource
) : BaseViewModel() {

    private val _viewState = MutableSharedFlow<ViewState>()
    val viewState = _viewState.asLiveData()

    private val _images = MutableStateFlow(emptyList<ZipoImage>())
    val images = _images.asLiveData()

    fun loadImages() =
        viewModelScope.launch {
            _viewState.emit(ViewState.Loading)

            imageDataSource.loadImages()
                .catch { _viewState.emit(ViewState.Failed) }
                .onCompletion { _viewState.emit(ViewState.Loaded) }
                .collect {
                    _images.emit(it)
                }
        }

    sealed interface ViewState {
        object Loading : ViewState
        object Loaded : ViewState
        object Failed : ViewState
    }
}