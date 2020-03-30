package com.gitspark.gitspark.ui.main.issues

import com.gitspark.gitspark.ui.adapter.EventComment

data class IssueRecyclerViewState(
    val events: MutableList<EventComment> = mutableListOf(),
    val commentsFinishedLoading: Boolean = false,
    val eventsFinishedLoading: Boolean = false,
    val isLastPage: Boolean = false
)