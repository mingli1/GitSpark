package com.gitspark.gitspark.ui.main.tab.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.ui.adapter.RepoDetailNavigator
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class ReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
) : BaseViewModel(), RepoDetailNavigator {

    val viewState = MutableLiveData<ReposViewState>()
    val userMediator = MediatorLiveData<AuthUser>()
    val navigateToRepoDetailAction = SingleLiveEvent<String>()

    private var resumed = false
    private var page = 1
    @VisibleForTesting var username: String? = null

    fun onResume(username: String? = null, user: User? = null) {
        this.username = username
        if (username == null) {
            val userData = userRepository.getCurrentUserData()
            userMediator.addSource(userData) { userMediator.value = it }
        }

        if (!resumed) {
            user?.let { viewState.value = ReposViewState(totalRepos = it.numPublicRepos) }
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

    override fun onRepoSelected(fullName: String) {
        navigateToRepoDetailAction.value = fullName
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
        if (reset) page = 1
        username?.let { requestRepos(it) } ?: requestAuthRepos()
    }

    private fun requestAuthRepos() {
        subscribe(repoRepository.getAuthRepos(page = page)) { handleGetReposResult(it) }
    }

    private fun requestRepos(username: String) {
        subscribe(repoRepository.getRepos(username, page = page)) { handleGetReposResult(it) }
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