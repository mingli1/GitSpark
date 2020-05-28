package com.gitspark.gitspark.ui.main.issues.pullrequest

import com.gitspark.gitspark.model.CheckSuite

data class ChecksViewState(
    val state: CheckState = CheckState.Pending,
    val checks: List<CheckSuite> = emptyList(),
    val showChecks: Boolean = false,
    val showChecksList: Boolean = true,
    val numPassed: Int = 0,
    val numFailed: Int = 0,
    val numPending: Int = 0
)

enum class CheckState {
    Failed,
    Pending,
    Success
}