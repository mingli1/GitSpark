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

class ReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<ReposViewState>()
    val repoDataMediator = MediatorLiveData<List<Repo>>()

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
        else updateViewStateWith(repos)
    }

    fun onRefresh() {
        viewState.value = viewState.value?.copy(refreshing = true)
        requestAuthRepos(null)
    }

    private fun requestAuthRepos(existingRepos: List<Repo>?) {
        subscribe(repoRepository.getAuthRepos(prefsHelper.getCachedToken())) {
            when (it) {
                is RepoResult.Success -> {
                    subscribe(repoRepository.cacheRepos(it.repos),
                        { updateViewStateWith(it.repos) },
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
        viewState.value = ReposViewState(
            repos = repos,
            loading = false,
            refreshing = false
        )
    }
}