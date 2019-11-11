package com.gitspark.gitspark.ui.main.home

import com.gitspark.gitspark.model.Event

data class HomeViewState(
    val recentEvents: List<Event> = emptyList(),
    val allEvents: ArrayList<Event> = arrayListOf(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false,
    val fullName: String = "",
    val username: String = "",
    val avatarUrl: String = ""
)