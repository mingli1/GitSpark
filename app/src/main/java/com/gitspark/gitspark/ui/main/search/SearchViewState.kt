package com.gitspark.gitspark.ui.main.search

import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.ui.adapter.Pageable

data class SearchViewState(
    val currSearch: SearchCriteria? = null,
    val recentSearches: List<SearchCriteria> = emptyList(),
    val searchResults: ArrayList<Pageable> = arrayListOf(),
    val resultsCount: Int = 0,
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false
)