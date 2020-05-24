package com.gitspark.gitspark.ui.main.issues

data class IssueEditViewState(
    val creating: Boolean = false,
    val title: String = "",
    val body: String = "",
    val assignees: List<String> = emptyList(),
    val labels: List<String> = emptyList(),
    val reviewers: List<String> = emptyList(),
    val loading: Boolean = false
)