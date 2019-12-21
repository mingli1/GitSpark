package com.gitspark.gitspark.ui.main.issues

import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.User

data class IssueDetailViewState(
    val issueTitle: String = "",
    val isOpen: Boolean = true,
    val issueDesc: String = "",
    val numComments: Int = 0,
    val labels: List<Label> = emptyList(),
    val assignees: List<User> = emptyList(),
    val loading: Boolean = false
)