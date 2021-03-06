package com.gitspark.gitspark.ui.base

import androidx.lifecycle.ViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

abstract class BaseViewModel : ViewModel() {

    private var initialized = false
    private val subscriptions = CompositeDisposable()

    val alertAction = SingleLiveEvent<String>()

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

    protected fun <T> subscribe(single: Single<T>, onSuccess: (T) -> Unit) {
        subscriptions.add(
            single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess)
        )
    }

    protected fun subscribe(
        completable: Completable,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit)
    {
        subscriptions.add(
            completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete, onError)
        )
    }

    protected fun alert(message: String) {
        alertAction.value = message
    }
}