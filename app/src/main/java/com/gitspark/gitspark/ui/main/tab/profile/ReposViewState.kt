package com.gitspark.gitspark.ui.main.tab.profile

import com.gitspark.gitspark.model.Repo

data class ReposViewState(
    val repos: List<Repo> = emptyList(),
    val loading: Boolean = false,
    val refreshing: Boolean = false
)