package com.gitspark.gitspark.ui.main.shared

data class ListViewState<T>(
    val list: ArrayList<T> = arrayListOf(),
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false
)