package com.gitspark.gitspark.model

const val MERGABLE_STATE_CLEAN = "clean"
const val MERGABLE_STATE_DIRTY = "dirty"
const val MERGABLE_STATE_UNSTABLE = "unstable"
const val MERGABLE_STATE_BLOCKED = "blocked"

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
    val requestedReviewers: List<User> = emptyList(),
    val head: PRBranch = PRBranch(),
    val base: PRBranch = PRBranch(),
    val draft: Boolean = false,
    val merged: Boolean = false,
    val mergeable: Boolean = false,
    val rebaseable: Boolean = false,
    val mergeableState: String = "",
    val mergedBy: User = User(),
    val numComments: Int = 0,
    val numReviewComments: Int = 0,
    val maintainerCanModify: Boolean = false,
    val numCommits: Int = 0,
    val numAdditions: Int = 0,
    val numDeletions: Int = 0,
    val numFilesChanged: Int = 0
)

data class PRBranch(
    val label: String = "",
    val ref: String = "",
    val sha: String = "",
    val user: User = User(),
    val repo: Repo = Repo()
)