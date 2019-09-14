package com.gitspark.gitspark.ui.main.profile

import com.gitspark.gitspark.model.Repo

data class StarsViewState(
    val repos: ArrayList<Repo> = arrayListOf(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val updateAdapter: Boolean = false,
    val totalStarred: Int = 0,
    val isLastPage: Boolean = false
)