package com.gitspark.gitspark.ui.main.profile

import com.gitspark.gitspark.model.Repo

data class ReposViewState(
    val repos: ArrayList<Repo> = arrayListOf(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val totalRepos: Int = 0,
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false
)