package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.Repo

data class RepoListViewState(
    val repos: ArrayList<Repo> = arrayListOf(),
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false
)