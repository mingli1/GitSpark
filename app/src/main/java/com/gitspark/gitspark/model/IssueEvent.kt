package com.gitspark.gitspark.model

import com.gitspark.gitspark.helper.COMMENTED_EVENT
import com.gitspark.gitspark.helper.COMMIT_EVENT
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_ISSUE_EVENT
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class IssueEvent(
    val id: Long = 0,
    val htmlUrl: String = "",
    val actor: User = User(),
    val event: String = "",
    val createdAt: String = "",
    val assignee: User = User(),
    val assignees: List<User> = emptyList(),
    val assigner: User = User(),
    val label: Label = Label(),
    val dismissedReview: DismissedReview = DismissedReview(),
    val rename: Rename = Rename(),
    val sha: String = "",
    val commitId: String = "",
    val committer: CommitUser = CommitUser(),
    val message: String = "",
    val verification: CommitVerification = CommitVerification(),
    var body: String = "",
    val reviewRequester: User = User(),
    val requestedReviewer: User = User()
) : Pageable {

    override fun getViewType() = if (isComment()) VIEW_TYPE_VIEW else VIEW_TYPE_ISSUE_EVENT

    override fun areItemsTheSame(other: Pageable) = this == (other as? IssueEvent ?: false)

    fun isComment() = event == COMMENTED_EVENT

    fun isCommit() = event == COMMIT_EVENT
}

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