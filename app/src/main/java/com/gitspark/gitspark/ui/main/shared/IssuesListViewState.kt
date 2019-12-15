package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.Issue

data class IssuesListViewState(
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val issues: ArrayList<Issue> = arrayListOf(),
    val isLastPage: Boolean = false,
    val updateAdapter: Boolean = false,
    val showOpenIssues: Boolean = true
)