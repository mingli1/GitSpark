package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
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
    private var username: String? = null

    fun onResume(username: String? = null) {
        if (username == null) {
            val userData = userRepository.getCurrentUserData()
            userMediator.addSource(userData) { userMediator.value = it }

            if (!resumed) {
                updateViewState(reset = true)
                resumed = true
            }
        }
        else {
            this.username = username
            if (!resumed) {
                updateViewState(reset = true, username = username)
                resumed = true
            }
        }
    }

    fun onDestroyView() {
        resumed = false
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true, username = username)

    fun onScrolledToEnd() = updateViewState(username = username)

    fun onUserDataRetrieved(user: AuthUser) {
        viewState.value = ReposViewState(
            totalRepos = user.numPublicRepos + user.totalPrivateRepos
        )
    }

    private fun updateViewState(
        reset: Boolean = false,
        refresh: Boolean = false,
        username: String? = null
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
        subscribe(repoRepository.getAuthRepos(page = page)) {
            when (it) {
                is RepoResult.Success -> {
                    updateWithRepoData(it.value.value, page, it.value.last)
                    if (page < it.value.last) page++
                }
                is RepoResult.Failure -> onGetRepoFailure(it.error)
            }
        }
    }

    private fun requestRepos(username: String) {
        subscribe(repoRepository.getRepos(username, page = page)) {
            when (it) {
                is RepoResult.Success -> {
                    updateWithRepoData(it.value.value, page, it.value.last)
                    if (page < it.value.last) page++
                }
                is RepoResult.Failure -> onGetRepoFailure(it.error)
            }
        }
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

    private fun updateWithRepoData(repos: List<Repo>, page: Int, last: Int) {
        viewState.value = viewState.value?.copy(
            repos = repos,
            loading = false,
            refreshing = false,
            isFirstPage = page.isFirstPage(),
            isLastPage = page.isLastPage(last),
            updateAdapter = true
        )
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