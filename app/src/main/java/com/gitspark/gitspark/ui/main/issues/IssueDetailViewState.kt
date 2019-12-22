package com.gitspark.gitspark.ui.main.issues

import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.adapter.EventComment

data class IssueDetailViewState(
    val issueTitle: String = "",
    val isOpen: Boolean = true,
    val issueDesc: String = "",
    val numComments: Int = 0,
    val labels: List<Label> = emptyList(),
    val assignees: List<User> = emptyList(),
    val locked: Boolean = false,
    val authorAvatarUrl: String = "",
    val authorUsername: String = "",
    val authorComment: String = "",
    val authorCommentDate: String = "",
    val commentsFinishedLoading: Boolean = false,
    val eventsFinishedLoading: Boolean = false,
    val events: ArrayList<EventComment> = arrayListOf(),
    val loading: Boolean = false,
    val isLastPage: Boolean = false,
    val updateAdapter: Boolean = false
)