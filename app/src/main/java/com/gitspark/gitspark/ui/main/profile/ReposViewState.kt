package com.gitspark.gitspark.ui.main.profile

data class ReposViewState(
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val totalRepos: Int = 0
)