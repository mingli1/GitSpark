package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.extension.concatWithPlus
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

internal const val REPOS = 0
internal const val USERS = 1
internal const val CODE = 2
internal const val COMMITS = 3
internal const val ISSUES = 4
internal const val PULL_REQUESTS = 5

class SearchFilterViewModel @Inject constructor() : BaseViewModel() {

    val viewState = MutableLiveData<SearchFilterViewState>()
    val clearAction = SingleLiveAction()
    val clearMainQueryAction = SingleLiveAction()
    val searchAction = SingleLiveEvent<String>()

    fun onSearchTypeSelected(type: Int) {
        viewState.value = viewState.value?.copy(currSearch = type) ?: SearchFilterViewState(currSearch = type)
    }

    fun onMainQueryClearButtonClicked() = clearMainQueryAction.call()

    fun onClearFieldsButtonClicked() = clearAction.call()

    fun onSearch(
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
        includeForked: Boolean,
        isOpen: Boolean,
        numComments: String,
        labels: String
    ) {
        val created = if (createdOn.isNotEmpty()) "created:$createdOn" else ""
        val lang = if (language.isNotEmpty()) "language:$language" else ""
        val user = if (fromThisUser.isNotEmpty()) "user:$fromThisUser" else ""
        val pushed = if (updatedOn.isNotEmpty()) "pushed:$updatedOn" else ""
        val updated = if (updatedOn.isNotEmpty()) "updated:$updatedOn" else ""
        val fork = "fork:$includeForked"
        val author = if (fromThisUser.isNotEmpty()) "author:$fromThisUser" else ""
        val comments = if (numComments.isNotEmpty()) "comments:$numComments" else ""
        val state = if (isOpen) "state:open" else "state:closed"
        val label = if (labels.isNotEmpty()) "label:$labels" else ""

        when (viewState.value?.currSearch ?: REPOS) {
            REPOS -> {
                val stars = if (numStars.isNotEmpty()) "stars:$numStars" else ""
                val forks = if (numForks.isNotEmpty()) "forks:$numForks" else ""
                val q = concatWithPlus(mainQuery, user, created, pushed, lang, stars, forks, fork)
            }
            USERS -> {
                val fname = if (fullName.isNotEmpty()) "fullname:$fullName" else ""
                val loc = if (location.isNotEmpty()) "location:$location" else ""
                val followers = if (numFollowers.isNotEmpty()) "followers:$numFollowers" else ""
                val repos = if (numRepos.isNotEmpty()) "repos:$numRepos" else ""
                val q = concatWithPlus(mainQuery, fname, loc, followers, repos, created, lang)
            }
            CODE -> {
                val extension = if (fileExtension.isNotEmpty()) "extension:$fileExtension" else ""
                val size = if (fileSize.isNotEmpty()) "size:$fileSize" else ""
                val q = concatWithPlus(mainQuery, user, lang, extension, size, fork)
            }
            COMMITS -> {
                val committer = if (fromThisUser.isNotEmpty()) "committer:$fromThisUser" else ""
                val committerDate = if (createdOn.isNotEmpty()) "committer-date:$createdOn" else ""
                val repo = if (repoFullName.isNotEmpty()) "repo:$repoFullName" else ""
                val q = concatWithPlus(mainQuery, committer, committerDate, lang, repo)
            }
            ISSUES -> {
                val type = "type:issue"
                val q = concatWithPlus(mainQuery, type, author, created, updated, lang, comments, state, label)
            }
            PULL_REQUESTS -> {
                val type = "type:pr"
                val q = concatWithPlus(mainQuery, type, author, created, updated, lang, comments, state, label)
            }
        }
    }
}