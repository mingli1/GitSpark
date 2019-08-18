package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.SORT_PUSHED
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

private const val SORT_ALL = "all"
private const val SORT_PUBLIC = "public"
private const val SORT_PRIVATE = "private"

class ReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<ReposViewState>()
    val repoDataMediator = MediatorLiveData<List<Repo>>()

    private var currentRepoData = emptyList<Repo>()
    private var filterString = ""
    private var sortSelection = SORT_ALL

    fun onResume() {
        val repoData = repoRepository.getRepos(order = SORT_PUSHED)
        repoDataMediator.addSource(repoData) { repoDataMediator.value = it }
    }

    fun onCachedRepoDataRetrieved(repos: List<Repo>) {
        if (repos.isEmpty()) return // TODO: handle empty case
        val expired = repoRepository.isRepoCacheExpired(repos[0].timestamp)
        if (expired) {
            viewState.value = ReposViewState(loading = true)
            requestAuthRepos(repos)
        }
        else {
            updateViewStateWith(repos)
        }
    }

    fun onRefresh() {
        viewState.value = viewState.value?.copy(
            refreshing = true,
            clearSearchFilter = true,
            clearSortSelection = true
        )
        requestAuthRepos(null)
    }

    fun onAfterTextChanged(text: String) {
        filterString = text
        updateViewStateWithFiltered()
    }

    fun onSortItemSelected(selection: String) {
        sortSelection = selection
        updateViewStateWithFiltered()
    }

    private fun requestAuthRepos(existingRepos: List<Repo>?) {
        subscribe(repoRepository.getAuthRepos(prefsHelper.getCachedToken())) {
            when (it) {
                is RepoResult.Success -> {
                    subscribe(repoRepository.cacheRepos(it.repos),
                        { updateViewStateWith(sortReposByUpdated(it.repos)) },
                        {
                            alert("Failed to cache repo data.")
                            existingRepos?.let { repo -> updateViewStateWith(repo) }
                        })
                }
                is RepoResult.Failure -> {
                    alert("Failed to update repo data.")
                    existingRepos?.let { repo -> updateViewStateWith(repo) }
                }
            }
        }
    }

    private fun updateViewStateWith(repos: List<Repo>) {
        currentRepoData = repos
        viewState.value = ReposViewState(
            repos = repos,
            loading = false,
            refreshing = false
        )
        requestStarredRepos()
    }

    private fun updateViewStateWithFiltered() {
        viewState.value?.let {
            viewState.value = it.copy(
                repos = filterRepos(currentRepoData, filterString, sortSelection),
                clearSortSelection = false,
                clearSearchFilter = false
            )
        }
    }

    private fun requestStarredRepos() {
        subscribe(repoRepository.getAuthStarredRepos(prefsHelper.getCachedToken())) { result ->
            when (result) {
                is RepoResult.Success -> {
                    currentRepoData.forEach { userRepo ->
                        if (result.repos.find { it.repoId == userRepo.repoId } != null) {
                            userRepo.starred = true
                        }
                    }
                    viewState.value = viewState.value?.copy(
                        repos = currentRepoData,
                        clearSortSelection = false,
                        clearSearchFilter = false
                    )
                }
                is RepoResult.Failure -> alert("Could not obtain repo starred data.")
            }
        }
    }

    private fun filterRepos(
        repos: List<Repo>,
        filter: String = "",
        sortType: String = SORT_ALL
    ): List<Repo> {
        val filteredRepos = repos.filter {
            it.repoName.startsWith(filter.trim(), ignoreCase = true)
        }
        return when (sortType) {
            SORT_ALL -> filteredRepos
            SORT_PUBLIC -> filteredRepos.filter { !it.isPrivate }
            SORT_PRIVATE -> filteredRepos.filter { it.isPrivate }
            else -> filteredRepos.sortedWith(Comparator { r1, r2 ->
                r2.numStars - r1.numStars
            })
        }
    }

    private fun sortReposByUpdated(repos: List<Repo>): List<Repo> {
        return repos.sortedWith(Comparator { r1, r2 ->
            r2.repoPushedAt.compareTo(r1.repoPushedAt)
        })
    }
}