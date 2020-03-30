package com.gitspark.gitspark.ui.main.profile

data class StarsViewState(
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val totalStarred: Int = 0
)