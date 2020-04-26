package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.SearchRepository
import com.gitspark.gitspark.repository.SearchResult
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.nav.IssueDetailNavigator
import com.gitspark.gitspark.ui.nav.RecentSearchNavigator
import com.gitspark.gitspark.ui.nav.RepoDetailNavigator
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val timeHelper: TimeHelper
) : BaseViewModel(), RepoDetailNavigator, UserProfileNavigator, RecentSearchNavigator, IssueDetailNavigator {

    val viewState = MutableLiveData<SearchViewState>()
    val pageViewState = MutableLiveData<PaginatedViewState<Pageable>>()
    val recentSearchesMediator = MediatorLiveData<List<SearchCriteria>>()
    val navigateToSearchFilter = SingleLiveEvent<SearchCriteria?>()
    val navigateToUserProfile = SingleLiveEvent<String>()
    val navigateToRepoDetail = SingleLiveEvent<Repo>()
    val navigateToIssueDetail = SingleLiveEvent<Triple<String, Issue, Boolean>>()
    private var currSearch: SearchCriteria? = null
    private var page = 1

    fun retrieveRecentSearches() {
        val rs = searchRepository.getRecentSearches()
        recentSearchesMediator.addSource(rs) { recentSearchesMediator.value = it }
    }

    fun onRecentSearchesRetrieved(rs: List<SearchCriteria>) {
        viewState.value = viewState.value?.copy(recentSearches = rs)
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
            resultsCount = data.second.totalCount
        ) ?: SearchViewState(
            currSearch = data.first,
            resultsCount = data.second.totalCount
        )
        pageViewState.value = pageViewState.value?.copy(
            items = data.second.value as MutableList<Pageable>,
            isLastPage = data.second.last == -1
        ) ?: PaginatedViewState(
            items = data.second.value as MutableList<Pageable>,
            isLastPage = data.second.last == -1
        )
    }

    fun onScrolledToEnd() = requestSearch()

    fun onClearResultsButtonClicked() {
        currSearch = null
        page = 1
        viewState.value = viewState.value?.copy(
            currSearch = null,
            resultsCount = 0
        )
        pageViewState.value = pageViewState.value?.copy(items = mutableListOf()) ?: PaginatedViewState()
    }

    fun onRefresh() {
        page = 1
        viewState.value = viewState.value?.copy(loading = true, refreshing = true)
        requestSearch()
    }

    override fun onRepoSelected(repo: Repo) {
        navigateToRepoDetail.value = repo
    }

    override fun onUserSelected(username: String) {
        navigateToUserProfile.value = username
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

    override fun onIssueClicked(issue: Issue, isPullRequest: Boolean) {
        navigateToIssueDetail.value = Triple(
            "${issue.getRepoFullNameFromUrl()} #${issue.number}",
            issue,
            isPullRequest
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
                val updatedList = if (page.isFirstPage()) mutableListOf() else pageViewState.value?.items ?: mutableListOf()
                updatedList.addAll((result.value as Page<Pageable>).value)

                viewState.value = viewState.value?.copy(
                    currSearch = currSearch,
                    resultsCount = result.value.totalCount,
                    loading = false,
                    refreshing = false
                )
                pageViewState.value = pageViewState.value?.copy(
                    items = updatedList,
                    isLastPage = page.isLastPage(result.value.last)
                ) ?: PaginatedViewState(
                    items = updatedList,
                    isLastPage = page.isLastPage(result.value.last)
                )
                if (page < result.value.last) page++
            }
            is SearchResult.Failure -> {
                alert(result.error)
                viewState.value = viewState.value?.copy(loading = false, refreshing = false)
            }
        }
    }
}