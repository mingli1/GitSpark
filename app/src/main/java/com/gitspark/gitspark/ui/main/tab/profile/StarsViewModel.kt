package com.gitspark.gitspark.ui.main.tab.profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

internal const val SORT_RECENT = "recent"
internal const val SORT_STARS = "stars"

class StarsViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<StarsViewState>()

    @VisibleForTesting var currentRepoData = emptyList<Repo>()
    private var filterString = ""
    private var sortSelection = SORT_RECENT

    fun onResume() {
        viewState.value = StarsViewState(loading = true)
        requestStarredRepos()
    }

    fun onRefresh() {
        viewState.value = viewState.value?.copy(
            refreshing = true,
            clearSearchFilter = true,
            clearSortSelection = true
        )
        requestStarredRepos()
    }

    fun onAfterTextChanged(text: String) {
        filterString = text
        updateViewStateWithFiltered()
    }

    fun onSortItemSelected(selection: String) {
        sortSelection = selection
        updateViewStateWithFiltered()
    }

    private fun requestStarredRepos() {
        subscribe(repoRepository.getAuthStarredRepos(prefsHelper.getCachedToken())) {
            when (it) {
                is RepoResult.Success -> {
                    currentRepoData = sortReposByRecent(it.value.value)
                    viewState.value = viewState.value?.copy(
                        repos = currentRepoData,
                        loading = false,
                        refreshing = false,
                        clearSearchFilter = false,
                        clearSortSelection = false
                    )
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false,
                        clearSearchFilter = false,
                        clearSortSelection = false
                    )
                }
            }
        }
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

    private fun filterRepos(
        repos: List<Repo>,
        filter: String = "",
        sortType: String = SORT_RECENT
    ): List<Repo> {
        val filteredRepos = repos.filter {
            it.repoName.startsWith(filter.trim(), ignoreCase = true)
        }
        return when (sortType) {
            SORT_RECENT -> filteredRepos
            SORT_STARS -> filteredRepos.sortedWith(Comparator { r1, r2 ->
                r2.numStars - r1.numStars
            })
            else -> filteredRepos.sortedWith(Comparator { r1, r2 ->
                r2.repoPushedAt.compareTo(r1.repoPushedAt)
            })
        }
    }

    private fun sortReposByRecent(repos: List<Repo>): List<Repo> {
        return repos.sortedWith(Comparator { r1, r2 ->
            r2.starredAt.compareTo(r1.starredAt)
        })
    }
}