package com.gitspark.gitspark.ui.main.issues

import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.model.User

data class IssueEditViewState(
    val title: String = "",
    val body: String = "",
    val assignees: List<User> = emptyList(),
    val labels: List<Label> = emptyList(),
    val updateContainers: Boolean = false
)