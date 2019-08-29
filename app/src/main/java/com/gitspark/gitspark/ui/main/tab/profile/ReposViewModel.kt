package com.gitspark.gitspark.ui.main.tab.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class ReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<ReposViewState>()
    val userMediator = MediatorLiveData<AuthUser>()

    private var resumed = false
    private var page = 1
    @VisibleForTesting var username: String? = null

    fun onResume(username: String? = null) {
        this.username = username
        if (username == null) {
            val userData = userRepository.getCurrentUserData()
            userMediator.addSource(userData) { userMediator.value = it }
        }

        if (!resumed) {
            updateViewState(reset = true)
            resumed = true
        }
    }

    fun onDestroyView() {
        resumed = false
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onScrolledToEnd() = updateViewState()

    fun onUserDataRetrieved(user: AuthUser) {
        viewState.value = ReposViewState(
            totalRepos = user.numPublicRepos + user.totalPrivateRepos
        )
    }

    private fun updateViewState(
        reset: Boolean = false,
        refresh: Boolean = false
    ) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: ReposViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) {
            page = 1
            username?.let { requestTotalRepos(it) }
        }
        username?.let { requestRepos(it) } ?: requestAuthRepos()
    }

    private fun requestAuthRepos() {
        subscribe(repoRepository.getAuthRepos(page = page)) { handleGetReposResult(it) }
    }

    private fun requestRepos(username: String) {
        subscribe(repoRepository.getRepos(username, page = page)) { handleGetReposResult(it) }
    }

    private fun requestTotalRepos(username: String) {
        subscribe(repoRepository.getRepos(username, page = 1, perPage = 1)) {
            when (it) {
                is RepoResult.Success -> {
                    val total = when {
                        it.value.last == -1 -> it.value.value.size
                        else -> it.value.last
                    }
                    viewState.value = viewState.value?.copy(
                        totalRepos = total,
                        loading = false,
                        refreshing = false,
                        updateAdapter = false
                    )
                }
            }
        }
    }

    private fun handleGetReposResult(it: RepoResult<Page<Repo>>) {
        when (it) {
            is RepoResult.Success -> updateWithRepoData(it.value.value, it.value.last)
            is RepoResult.Failure -> onGetRepoFailure(it.error)
        }
    }

    private fun updateWithRepoData(repos: List<Repo>, last: Int) {
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

    private fun onGetRepoFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(
            loading = false,
            refreshing = false,
            updateAdapter = false
        )
    }
}