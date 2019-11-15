package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.ui.base.BaseViewModel

interface ScrollingViewModel {
    fun onScrolledToEnd()
    fun onRefresh()
    fun onDestroy()
}

abstract class ListViewModel<S> : BaseViewModel(), ScrollingViewModel {

    val viewState = MutableLiveData<ListViewState<S>>()
    private var started = false
    protected var page = 1

    protected var args = ""

    override fun onRefresh() = updateViewState(reset = true, refresh = true)

    override fun onScrolledToEnd() = updateViewState()

    override fun onDestroy() {
        started = false
    }

    private fun updateViewState(reset: Boolean = false, refresh: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: ListViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) page = 1
        requestData()
    }

    protected fun start() {
        if (!started) {
            updateViewState(reset = true)
            started = true
        }
    }

    protected abstract fun requestData()

    protected fun onDataSuccess(toAdd: List<S>, last: Int) {
        val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.list ?: arrayListOf()
        updatedList.addAll(toAdd)

        viewState.value = viewState.value?.copy(
            list = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true,
            loading = false,
            refreshing = false
        ) ?: ListViewState(
            list = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true,
            loading = false,
            refreshing = false
        )
        if (page < last) page++
    }

    protected fun onDataFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(updateAdapter = false, loading = false, refreshing = false)
            ?: ListViewState(updateAdapter = false, loading = false, refreshing = false)
    }
}