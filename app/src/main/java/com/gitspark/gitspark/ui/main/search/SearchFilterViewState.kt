package com.gitspark.gitspark.ui.main.search

data class SearchFilterViewState(
    val currSearch: Int = REPOS,
    val loading: Boolean = false
)