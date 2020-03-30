package com.gitspark.gitspark.ui.main.shared

data class IssuesListViewState(
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val showOpenIssues: Boolean = true,
    val numOpen: Int = 0,
    val numClosed: Int = 0
)