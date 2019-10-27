package com.gitspark.gitspark.model

data class PullRequest(
    val number: Int = 0,
    val state: String = "",
    val locked: Boolean = false,
    val title: String = "",
    val user: User = User(),
    val body: String = "",
    val labels: List<Label> = emptyList(),
    val lockReason: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val closedAt: String = "",
    val mergedAt: String = "",
    val mergeSha: String = "",
    val assignee: User = User(),
    val assignees: List<User> = emptyList(),
    val requestReviewers: List<User> = emptyList(),
    val head: PRBranch = PRBranch(),
    val base: PRBranch = PRBranch(),
    val draft: Boolean = false
)

data class PRBranch(
    val label: String = "",
    val ref: String = "",
    val sha: String = "",
    val user: User = User(),
    val repo: Repo = Repo()
)