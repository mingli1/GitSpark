package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.base.PaginatedViewState

interface ScrollingViewModel {
    fun onScrolledToEnd()
    fun onRefresh()
    fun onDestroy()
}

abstract class ListViewModel<S> : BaseViewModel(), ScrollingViewModel {

    val viewState = MutableLiveData<ListViewState>()
    val pageViewState = MutableLiveData<PaginatedViewState<S>>()
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
            refreshing = refresh
        ) ?: ListViewState(
            loading = reset,
            refreshing = refresh
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
        val updatedList = if (page.isFirstPage()) mutableListOf() else pageViewState.value?.items ?: mutableListOf()
        updatedList.addAll(toAdd)

        viewState.value = viewState.value?.copy(
            loading = false,
            refreshing = false
        ) ?: ListViewState(
            loading = false,
            refreshing = false
        )
        pageViewState.value = pageViewState.value?.copy(
            items = updatedList,
            isLastPage = page.isLastPage(last)
        ) ?: PaginatedViewState(
            items = updatedList,
            isLastPage = page.isLastPage(last)
        )
        if (page < last) page++
    }

    protected fun onDataFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(loading = false, refreshing = false)
            ?: ListViewState(loading = false, refreshing = false)
    }
}