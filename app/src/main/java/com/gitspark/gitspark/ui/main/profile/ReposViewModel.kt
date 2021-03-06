package com.gitspark.gitspark.ui.main.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.ui.nav.RepoDetailNavigator
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class ReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
) : BaseViewModel(), RepoDetailNavigator {

    val viewState = MutableLiveData<ReposViewState>()
    val pageViewState = MutableLiveData<PaginatedViewState<Repo>>()
    val userMediator = MediatorLiveData<AuthUser>()
    val navigateToRepoDetailAction = SingleLiveEvent<Repo>()

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

    fun onDestroy() {
        resumed = false
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onScrolledToEnd() = updateViewState()

    fun onUserDataRetrieved(user: AuthUser) {
        viewState.value = viewState.value?.copy(
            totalRepos = user.numPublicRepos + user.totalPrivateRepos
        ) ?: ReposViewState(
            totalRepos = user.numPublicRepos + user.totalPrivateRepos
        )
    }

    fun onRepoDataUpdated(newData: Repo) {
        val repos = pageViewState.value?.items?: return
        val index = repos.indexOfFirst { it.fullName == newData.fullName }
        if (index < 0) return
        repos[index] = newData

        pageViewState.value = pageViewState.value?.copy(items = repos) ?: PaginatedViewState(items = repos)
    }

    override fun onRepoSelected(repo: Repo) {
        navigateToRepoDetailAction.value = repo
    }

    private fun updateViewState(
        reset: Boolean = false,
        refresh: Boolean = false
    ) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh
        ) ?: ReposViewState(
            loading = reset,
            refreshing = refresh
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

    private fun updateWithRepoData(reposToAdd: List<Repo>, last: Int) {
        val updatedList = if (page.isFirstPage()) mutableListOf() else pageViewState.value?.items ?: mutableListOf()
        updatedList.addAll(reposToAdd)

        viewState.value = viewState.value?.copy(
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

    private fun onGetRepoFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(
            loading = false,
            refreshing = false
        )
    }
}