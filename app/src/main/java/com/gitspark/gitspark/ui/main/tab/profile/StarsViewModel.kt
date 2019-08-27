package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class StarsViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<StarsViewState>()

    private var resumed = false
    private var page = 1

    fun onResume() {
        if (!resumed) {
            updateViewState(reset = true, fetchTotal = true)
            resumed = true
        }
    }

    fun onDestroyView() {
        resumed = false
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true, fetchTotal = true)

    fun onScrolledToEnd() = updateViewState()

    private fun updateViewState(
        reset: Boolean = false,
        refresh: Boolean = false,
        fetchTotal: Boolean = false
    ) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: StarsViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) page = 1
        if (fetchTotal) requestTotalRepos()
        requestStarredRepos()
    }

    private fun requestStarredRepos() {
        subscribe(repoRepository.getAuthStarredRepos(prefsHelper.getCachedToken(), page)) {
            when (it) {
                is RepoResult.Success -> {
                    val isFirstPage = page == 1
                    val isLastPage = if (it.value.last == -1) true else page == it.value.last
                    viewState.value = viewState.value?.copy(
                        repos = it.value.value,
                        loading = false,
                        refreshing = false,
                        isFirstPage = isFirstPage,
                        isLastPage = isLastPage,
                        updateAdapter = true
                    )
                    if (page < it.value.last) page++
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false, updateAdapter = false)
                }
            }
        }
    }

    private fun requestTotalRepos() {
        subscribe(repoRepository.getAuthStarredRepos(prefsHelper.getCachedToken(), 1, 1)) {
            when (it) {
                is RepoResult.Success -> {
                    val total = when {
                        it.value.last == -1 -> it.value.value.size
                        else -> it.value.last
                    }
                    viewState.value = viewState.value?.copy(
                        totalStarred = total,
                        loading = false,
                        refreshing = false,
                        updateAdapter = false
                    )
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false,
                        updateAdapter = false
                    )
                }
            }
        }
    }
}