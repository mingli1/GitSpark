package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class StarsViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<StarsViewState>()

    private var resumed = false
    private var page = 1
    private var username: String? = null

    fun onResume(username: String?) {
        this.username = username
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
        if (fetchTotal) {
            username?.let { requestTotalRepos(it) } ?: requestTotalAuthRepos()
        }
        username?.let { requestStarredRepos(it) } ?: requestAuthStarredRepos()
    }

    private fun requestAuthStarredRepos() {
        subscribe(repoRepository.getAuthStarredRepos(page)) { handleGetReposResult(it) }
    }

    private fun requestStarredRepos(username: String) {
        subscribe(repoRepository.getStarredRepos(username, page = page)) { handleGetReposResult(it) }
    }

    private fun requestTotalAuthRepos() {
        subscribe(repoRepository.getAuthStarredRepos(page = 1, perPage = 1)) { handleGetTotalReposResult(it) }
    }

    private fun requestTotalRepos(username: String) {
        subscribe(repoRepository.getStarredRepos(username, page = 1, perPage = 1)) { handleGetTotalReposResult(it) }
    }

    private fun handleGetReposResult(it: RepoResult<Page<Repo>>) {
        when (it) {
            is RepoResult.Success -> onGetStarredReposSuccess(it.value.value, it.value.last)
            is RepoResult.Failure -> onReposFailure(it.error)
        }
    }

    private fun handleGetTotalReposResult(it: RepoResult<Page<Repo>>) {
        when (it) {
            is RepoResult.Success -> onTotalReposSuccess(it.value)
            is RepoResult.Failure -> onReposFailure(it.error)
        }
    }

    private fun onGetStarredReposSuccess(repos: List<Repo>, last: Int) {
        viewState.value = viewState.value?.copy(
            repos = repos,
            loading = false,
            refreshing = false,
            isFirstPage = page.isFirstPage(),
            isLastPage = page.isLastPage(last),
            updateAdapter = true
        )
        if (page < last) page++
    }

    private fun onTotalReposSuccess(page: Page<Repo>) {
        val total = when {
            page.last == -1 -> page.value.size
            else -> page.last
        }
        viewState.value = viewState.value?.copy(
            totalStarred = total,
            loading = false,
            refreshing = false,
            updateAdapter = false
        )
    }

    private fun onReposFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(
            loading = false,
            refreshing = false,
            updateAdapter = false
        )
    }
}