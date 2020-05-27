package com.gitspark.gitspark.ui.main.issues.pullrequest

import com.gitspark.gitspark.model.RepoStatus

data class ChecksViewState(
    val state: String = "",
    val checks: List<RepoStatus> = emptyList(),
    val showChecks: Boolean = false,
    val showChecksList: Boolean = true,
    val numPassed: Int = 0,
    val numFailed: Int = 0,
    val numPending: Int = 0
)