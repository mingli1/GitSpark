package com.gitspark.gitspark.ui.main.profile

import com.gitspark.gitspark.model.Event

data class ProfileFeedViewState(
    val events: ArrayList<Event> = arrayListOf(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val updateAdapter: Boolean = false,
    val isLastPage: Boolean = false
)