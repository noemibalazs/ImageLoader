package com.noemi.imageloader.base

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class BaseViewModel : ViewModel() {

    private val disposable = CompositeDisposable()

    protected fun Disposable.addDisposable() = disposable.add(this)

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}