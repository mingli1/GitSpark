package com.gitspark.gitspark.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

abstract class BaseViewModel : ViewModel() {

    private var initialized = false
    private val subscriptions = CompositeDisposable()

    fun checkInitialized() {
        if (!initialized) {
            initialize()
            initialized = true
        }
    }

    open fun initialize() {}

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }

    protected fun <T> subscribe(observable: Observable<T>, onNext: (T) -> Unit) {
        subscriptions.add(
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext)
        )
    }
}