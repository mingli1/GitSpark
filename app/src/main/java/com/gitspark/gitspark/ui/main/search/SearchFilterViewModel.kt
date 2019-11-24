package com.gitspark.gitspark.ui.main.search

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

internal const val REPOS = 0
internal const val USERS = 1
internal const val CODE = 2
internal const val COMMITS = 3
internal const val ISSUES = 4
internal const val PULL_REQUESTS = 5

class SearchFilterViewModel @Inject constructor() : BaseViewModel() {

    val viewState = MutableLiveData<SearchFilterViewState>()

    fun onSearchTypeSelected(type: Int) {
        viewState.value = viewState.value?.copy(currSearch = type) ?: SearchFilterViewState(currSearch = type)
    }
}