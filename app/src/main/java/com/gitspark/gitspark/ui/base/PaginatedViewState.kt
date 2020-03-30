package com.gitspark.gitspark.ui.base

data class PaginatedViewState<T>(
    val items: MutableList<T> = mutableListOf(),
    val isLastPage: Boolean = false
)