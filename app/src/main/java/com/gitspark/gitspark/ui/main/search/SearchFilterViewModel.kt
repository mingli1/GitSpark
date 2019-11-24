package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MutableLiveData
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
        commitMessage: String,
        repoFullName: String,
        includeForked: Boolean,
        isOpen: Boolean,
        numComments: String,
        labels: String
    ) {
        when (viewState.value?.currSearch ?: REPOS) {
            REPOS -> {

            }
        }
    }
}