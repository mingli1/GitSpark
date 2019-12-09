package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.SearchRepository
import com.gitspark.gitspark.repository.SearchResult
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.nav.RepoDetailNavigator
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel(), RepoDetailNavigator, UserProfileNavigator {

    val viewState = MutableLiveData<SearchViewState>()
    val navigateToSearchFilter = SingleLiveAction()
    private var currSearch: SearchCriteria? = null
    private var page = 1

    fun onSearchButtonClicked() = navigateToSearchFilter.call()

    fun onSearchResultsObtained(data: Pair<SearchCriteria, Page<Pageable>>) {
        currSearch = data.first
        if (page < data.second.last) page++

        viewState.value = viewState.value?.copy(
            currSearch = data.first,
            searchResults = data.second.value as ArrayList<Pageable>,
            resultsCount = data.second.totalCount,
            updateAdapter = true
        ) ?: SearchViewState(
            currSearch = data.first,
            searchResults = data.second.value as ArrayList<Pageable>,
            resultsCount = data.second.totalCount,
            updateAdapter = true
        )
    }

    fun onScrolledToEnd() = requestSearch()

    override fun onRepoSelected(repo: Repo) {

    }

    override fun onUserSelected(username: String) {

    }

    private fun requestSearch() {
        currSearch?.let { currSearch ->
            when (currSearch.type) {
                REPOS -> subscribe(searchRepository.searchRepos(currSearch.q, page)) { onSearchResult(it) }
                USERS -> subscribe(searchRepository.searchUsers(currSearch.q, page)) { onSearchResult(it) }
                CODE -> subscribe(searchRepository.searchCode(currSearch.q, page)) { onSearchResult(it) }
                COMMITS -> subscribe(searchRepository.searchCommits(currSearch.q, page)) { onSearchResult(it) }
                else -> subscribe(searchRepository.searchIssues(currSearch.q, page)) { onSearchResult(it) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> onSearchResult(result: SearchResult<T>) {
        when (result) {
            is SearchResult.Success -> {
                val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.searchResults ?: arrayListOf()
                updatedList.addAll((result.value as Page<Pageable>).value)

                viewState.value = viewState.value?.copy(
                    searchResults = updatedList,
                    isLastPage = page.isLastPage(result.value.last),
                    updateAdapter = true
                )
                if (page < result.value.last) page++
            }
            is SearchResult.Failure -> {
                alert(result.error)
                viewState.value = viewState.value?.copy(updateAdapter = false)
            }
        }
    }
}