package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.SearchRepository
import com.gitspark.gitspark.repository.SearchResult
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.nav.RecentSearchNavigator
import com.gitspark.gitspark.ui.nav.RepoDetailNavigator
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val timeHelper: TimeHelper
) : BaseViewModel(), RepoDetailNavigator, UserProfileNavigator, RecentSearchNavigator {

    val viewState = MutableLiveData<SearchViewState>()
    val recentSearchesMediator = MediatorLiveData<List<SearchCriteria>>()
    val navigateToSearchFilter = SingleLiveEvent<SearchCriteria?>()
    private var currSearch: SearchCriteria? = null
    private var page = 1

    fun retrieveRecentSearches() {
        val rs = searchRepository.getRecentSearches()
        recentSearchesMediator.addSource(rs) { recentSearchesMediator.value = it }
    }

    fun onRecentSearchesRetrieved(rs: List<SearchCriteria>) {
        viewState.value = viewState.value?.copy(recentSearches = rs, updateAdapter = false)
            ?: SearchViewState(recentSearches = rs)
    }

    fun onSearchButtonClicked() {
        navigateToSearchFilter.value = currSearch
    }

    fun onSearchResultsObtained(data: Pair<SearchCriteria, Page<Pageable>>) {
        currSearch = data.first
        if (page < data.second.last) page++

        subscribe(searchRepository.cacheSearch(data.first),
            { retrieveRecentSearches() },
            { alert("${it.message}") }
        )

        viewState.value = viewState.value?.copy(
            currSearch = data.first,
            searchResults = data.second.value as ArrayList<Pageable>,
            resultsCount = data.second.totalCount,
            isLastPage = data.second.last == -1,
            updateAdapter = true
        ) ?: SearchViewState(
            currSearch = data.first,
            searchResults = data.second.value as ArrayList<Pageable>,
            resultsCount = data.second.totalCount,
            isLastPage = data.second.last == -1,
            updateAdapter = true
        )
    }

    fun onScrolledToEnd() = requestSearch()

    fun onClearResultsButtonClicked() {
        currSearch = null
        page = 1
        viewState.value = viewState.value?.copy(
            currSearch = null,
            searchResults = arrayListOf(),
            resultsCount = 0,
            updateAdapter = true
        )
    }

    override fun onRepoSelected(repo: Repo) {

    }

    override fun onUserSelected(username: String) {

    }

    override fun onRecentSearchClicked(sc: SearchCriteria) {
        currSearch = sc
        page = 1

        viewState.value = viewState.value?.copy(loading = true)

        sc.timestamp = timeHelper.nowAsString()
        subscribe(searchRepository.cacheSearch(sc),
            { retrieveRecentSearches() },
            { alert("${it.message}") }
        )

        requestSearch()
    }

    override fun onRemoveSearchClicked(q: String) {
        subscribe(searchRepository.deleteSearch(q),
            { retrieveRecentSearches() },
            { alert("${it.message}") }
        )
    }

    private fun requestSearch() {
        currSearch?.let { currSearch ->
            when (currSearch.type) {
                REPOS -> subscribe(searchRepository.searchRepos(currSearch.q, page, currSearch.sort, currSearch.order)) { onSearchResult(it) }
                USERS -> subscribe(searchRepository.searchUsers(currSearch.q, page, currSearch.sort, currSearch.order)) { onSearchResult(it) }
                CODE -> subscribe(searchRepository.searchCode(currSearch.q, page, currSearch.sort, currSearch.order)) { onSearchResult(it) }
                COMMITS -> subscribe(searchRepository.searchCommits(currSearch.q, page, currSearch.sort, currSearch.order)) { onSearchResult(it) }
                else -> subscribe(searchRepository.searchIssues(currSearch.q, page, currSearch.sort, currSearch.order)) { onSearchResult(it) }
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
                    currSearch = currSearch,
                    searchResults = updatedList,
                    isLastPage = page.isLastPage(result.value.last),
                    updateAdapter = true,
                    resultsCount = result.value.totalCount,
                    loading = false
                )
                if (page < result.value.last) page++
            }
            is SearchResult.Failure -> {
                alert(result.error)
                viewState.value = viewState.value?.copy(updateAdapter = false, loading = false)
            }
        }
    }
}