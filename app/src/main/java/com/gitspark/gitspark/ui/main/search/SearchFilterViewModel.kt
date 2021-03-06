package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.extension.concatWithPlus
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.repository.SearchRepository
import com.gitspark.gitspark.repository.SearchResult
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

const val REPOS = 0
const val USERS = 1
const val CODE = 2
const val COMMITS = 3
const val ISSUES = 4
const val PULL_REQUESTS = 5

class SearchFilterViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<SearchFilterViewState>()
    val clearAction = SingleLiveAction()
    val clearMainQueryAction = SingleLiveAction()
    val searchAction = SingleLiveEvent<Pair<SearchCriteria, Page<Pageable>>>()
    var existingSearchCriteria: SearchCriteria? = null

    fun onSearchTypeSelected(type: Int) {
        viewState.value = viewState.value?.copy(currSearch = type) ?: SearchFilterViewState(currSearch = type)
    }

    fun onMainQueryClearButtonClicked() = clearMainQueryAction.call()

    fun onClearFieldsButtonClicked() = clearAction.call()

    fun onSearch(
        sort: String,
        order: String,
        mainQuery: String,
        createdOn: String,
        language: String,
        fromThisUser: String,
        fullName: String,
        location: String,
        numFollowers: String,
        numRepos: String,
        numStars: String,
        numForks: String,
        updatedOn: String,
        fileExtension: String,
        fileSize: String,
        repoFullName: String,
        fork: String,
        state: String,
        numComments: String,
        labels: String
    ) {
        viewState.value = viewState.value?.copy(loading = true)

        val created = if (createdOn.isNotEmpty()) "created:$createdOn" else ""
        val lang = if (language.isNotEmpty()) "language:$language" else ""
        val user = if (fromThisUser.isNotEmpty()) "user:$fromThisUser" else ""
        val pushed = if (updatedOn.isNotEmpty()) "pushed:$updatedOn" else ""
        val updated = if (updatedOn.isNotEmpty()) "updated:$updatedOn" else ""
        val qfork = "fork:$fork"
        val author = if (fromThisUser.isNotEmpty()) "author:$fromThisUser" else ""
        val comments = if (numComments.isNotEmpty()) "comments:$numComments" else ""
        val qstate = if (state.isEmpty()) "" else  "state:$state"
        val label = if (labels.isNotEmpty()) "label:$labels" else ""

        when (viewState.value?.currSearch ?: REPOS) {
            REPOS -> {
                val stars = if (numStars.isNotEmpty()) "stars:$numStars" else ""
                val forks = if (numForks.isNotEmpty()) "forks:$numForks" else ""
                val q = concatWithPlus(mainQuery, user, created, pushed, lang, stars, forks, qfork)
                if (!hasExistingSearch(q, sort, order)) {
                    subscribe(searchRepository.searchRepos(q, 1, sort, order)) {
                        onSearchResult(
                            SearchCriteria(sort, order,
                                REPOS, q.replace("+", " "), mainQuery, createdOn, language,
                                fromThisUser, fullName, location, numFollowers, numRepos, numStars, numForks, updatedOn,
                                fileExtension, fileSize, repoFullName, fork, state, numComments, labels
                            ), it
                        )
                    }
                }
            }
            USERS -> {
                val fname = if (fullName.isNotEmpty()) "fullname:$fullName" else ""
                val loc = if (location.isNotEmpty()) "location:$location" else ""
                val followers = if (numFollowers.isNotEmpty()) "followers:$numFollowers" else ""
                val repos = if (numRepos.isNotEmpty()) "repos:$numRepos" else ""
                val q = concatWithPlus(mainQuery, fname, loc, followers, repos, created, lang)
                if (!hasExistingSearch(q, sort, order)) {
                    subscribe(searchRepository.searchUsers(q, 1, sort, order)) {
                        onSearchResult(
                            SearchCriteria(sort, order,
                                USERS, q.replace("+", " "), mainQuery, createdOn, language,
                                fromThisUser, fullName, location, numFollowers, numRepos, numStars, numForks, updatedOn,
                                fileExtension, fileSize, repoFullName, fork, state, numComments, labels
                            ), it
                        )
                    }
                }
            }
            CODE -> {
                val extension = if (fileExtension.isNotEmpty()) "extension:$fileExtension" else ""
                val size = if (fileSize.isNotEmpty()) "size:$fileSize" else ""
                val q = concatWithPlus(mainQuery, user, lang, extension, size, qfork)
                if (!hasExistingSearch(q, sort, order)) {
                    subscribe(searchRepository.searchCode(q, 1, sort, order)) {
                        onSearchResult(
                            SearchCriteria(sort, order,
                                CODE, q.replace("+", " "), mainQuery, createdOn, language,
                                fromThisUser, fullName, location, numFollowers, numRepos, numStars, numForks, updatedOn,
                                fileExtension, fileSize, repoFullName, fork, state, numComments, labels
                            ), it
                        )
                    }
                }
            }
            COMMITS -> {
                val committer = if (fromThisUser.isNotEmpty()) "committer:$fromThisUser" else ""
                val committerDate = if (createdOn.isNotEmpty()) "committer-date:$createdOn" else ""
                val repo = if (repoFullName.isNotEmpty()) "repo:$repoFullName" else ""
                val q = concatWithPlus(mainQuery, committer, committerDate, lang, repo)
                if (!hasExistingSearch(q, sort, order)) {
                    subscribe(searchRepository.searchCommits(q, 1, sort, order)) {
                        onSearchResult(
                            SearchCriteria(sort, order,
                                COMMITS, q.replace("+", " "), mainQuery, createdOn, language,
                                fromThisUser, fullName, location, numFollowers, numRepos, numStars, numForks, updatedOn,
                                fileExtension, fileSize, repoFullName, fork, state, numComments, labels
                            ), it
                        )
                    }
                }
            }
            ISSUES -> {
                val type = "type:issue"
                val q = concatWithPlus(mainQuery, type, author, created, updated, lang, comments, qstate, label)
                if (!hasExistingSearch(q, sort, order)) {
                    subscribe(searchRepository.searchIssues(q, 1, sort, order)) {
                        onSearchResult(
                            SearchCriteria(sort, order,
                                ISSUES, q.replace("+", " "), mainQuery, createdOn, language,
                                fromThisUser, fullName, location, numFollowers, numRepos, numStars, numForks, updatedOn,
                                fileExtension, fileSize, repoFullName, fork, state, numComments, labels
                            ), it
                        )
                    }
                }
            }
            PULL_REQUESTS -> {
                val type = "type:pr"
                val q = concatWithPlus(mainQuery, type, author, created, updated, lang, comments, qstate, label)
                if (!hasExistingSearch(q, sort, order)) {
                    subscribe(searchRepository.searchIssues(q, 1, sort, order)) {
                        onSearchResult(
                            SearchCriteria(sort, order,
                                PULL_REQUESTS, q.replace("+", " "), mainQuery, createdOn, language,
                                fromThisUser, fullName, location, numFollowers, numRepos, numStars, numForks, updatedOn,
                                fileExtension, fileSize, repoFullName, fork, state, numComments, labels
                            ), it
                        )
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> onSearchResult(sc: SearchCriteria, result: SearchResult<T>) {
        when (result) {
            is SearchResult.Success -> {
                searchAction.value = Pair(sc, result.value as Page<Pageable>)
            }
            is SearchResult.Failure -> alert(result.error)
        }
        viewState.value = viewState.value?.copy(loading = false)
        existingSearchCriteria = null
    }

    private fun hasExistingSearch(q: String, sort: String, order: String): Boolean {
        if (q.replace("+", " ") == existingSearchCriteria?.q &&
            sort == existingSearchCriteria?.sort &&
            order == existingSearchCriteria?.order)
        {
            alert("Search criteria has not changed. Please enter a different search.")
            viewState.value = viewState.value?.copy(loading = false)
            return true
        }
        return false
    }
}