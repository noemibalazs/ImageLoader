package com.noemi.imageloader.ui

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator

@BindingAdapter("viewState")
fun bindViewState(progressIndicator: CircularProgressIndicator, viewState: MainViewModel.ViewState?) {
    progressIndicator.isVisible = viewState == MainViewModel.ViewState.Loading
}