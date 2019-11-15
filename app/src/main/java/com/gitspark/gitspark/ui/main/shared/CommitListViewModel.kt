package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class CommitListViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<ListViewState<Commit>>()
    private var resumed = false
    private var page = 1
    private var args = ""

    fun onResume(args: String) {
        if (args.isEmpty()) return
        this.args = args

        if (!resumed) {
            updateViewState(reset = true)
            resumed = true
        }
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onScrolledToEnd() = updateViewState()

    fun onDestroy() {
        resumed = false
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
        requestCommits()
    }

    private fun requestCommits() {
        val args = this.args.split("/")
        subscribe(repoRepository.getCommits(args[0], args[1], args[2], page = page)) {
            when (it) {
                is RepoResult.Success -> {
                    val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.list ?: arrayListOf()
                    updatedList.addAll(it.value.value)

                    viewState.value = viewState.value?.copy(
                        list = updatedList,
                        isLastPage = page.isLastPage(it.value.last),
                        updateAdapter = true,
                        loading = false,
                        refreshing = false
                    ) ?: ListViewState(
                        list = updatedList,
                        isLastPage = page.isLastPage(it.value.last),
                        updateAdapter = true,
                        loading = false,
                        refreshing = false
                    )
                    if (page < it.value.last) page++
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(updateAdapter = false, loading = false, refreshing = false)
                        ?: ListViewState(updateAdapter = false, loading = false, refreshing = false)
                }
            }
        }
    }
}