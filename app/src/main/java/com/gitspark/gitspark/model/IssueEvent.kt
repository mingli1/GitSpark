package com.gitspark.gitspark.model

data class IssueEvent(
    val id: Long = 0,
    val actor: User = User(),
    val event: String = "",
    val createdAt: String = "",
    val assignee: User = User(),
    val assignees: List<User> = emptyList(),
    val assigner: User = User(),
    val labels: List<Label> = emptyList(),
    val dismissedReview: DismissedReview = DismissedReview(),
    val rename: Rename = Rename()
)

data class DismissedReview(
    val state: String = "",
    val reviewId: Int = 0,
    val dismissalMessage: String = "",
    val dismissalCommitId: String = ""
)

data class Rename(
    val from: String = "",
    val to: String = ""
)