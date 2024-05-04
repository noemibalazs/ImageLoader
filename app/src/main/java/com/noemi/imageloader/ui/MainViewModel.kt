package com.noemi.imageloader.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.noemi.imageloader.base.BaseViewModel
import com.noemi.imageloader.model.ZipoImage
import com.noemi.imageloader.remotedatasource.ZipoImageDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val imageDataSource: ZipoImageDataSource
) : BaseViewModel() {

    private val _zipoImages = MutableLiveData<List<ZipoImage>>()
    val zipoImages: LiveData<List<ZipoImage>> = _zipoImages

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    fun loadImages() =
        Single.just(true)
            .doOnSubscribe { setViewState(ViewState.Loading) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { imageDataSource.loadImages() }
            .map { _zipoImages.postValue(it) }
            .subscribe({ setViewState(ViewState.Loaded) }) { setViewState(ViewState.Failed) }
            .addDisposable()

    private fun setViewState(state: ViewState) {

        _viewState.postValue(state)
    }

    sealed interface ViewState {
        object Loading : ViewState
        object Loaded : ViewState
        object Failed : ViewState
    }
}