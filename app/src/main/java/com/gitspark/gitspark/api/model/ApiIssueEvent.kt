package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.*
import com.squareup.moshi.Json

data class ApiIssueEvent(
    @field:Json(name = "id") val id: Long?,
    @field:Json(name = "html_url") val htmlUrl: String?,
    @field:Json(name = "actor") val actor: ApiUser?,
    @field:Json(name = "event") val event: String?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "assignee") val assignee: ApiUser?,
    @field:Json(name = "assignees") val assignees: List<ApiUser>?,
    @field:Json(name = "assigner") val assigner: ApiUser?,
    @field:Json(name = "label") val label: ApiLabel?,
    @field:Json(name = "dismissed_review") val dismissedReview: ApiDismissedReview?,
    @field:Json(name = "rename") val rename: ApiRename?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "commit_id") val commitId: String?,
    @field:Json(name = "committer") val committer: ApiCommitUser?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "verification") val verification: ApiCommitVerification?,
    @field:Json(name = "body") val body: String?,
    @field:Json(name = "review_requester") val reviewRequester: ApiUser?,
    @field:Json(name = "requested_reviewer") val requestedReviewer: ApiUser?,
    @field:Json(name = "state") val state: String?,
    @field:Json(name = "user") val user: ApiUser?,
    @field:Json(name = "submitted_at") val submittedAt: String?
) {
    fun toModel() = IssueEvent(
        id = id ?: 0,
        htmlUrl = htmlUrl ?: "",
        actor = actor?.toModel() ?: User(),
        event = event ?: "",
        createdAt = createdAt ?: "",
        assignee = assignee?.toModel() ?: User(),
        assignees = assignees?.map { it.toModel() } ?: emptyList(),
        assigner = assigner?.toModel() ?: User(),
        label = label?.toModel() ?: Label(),
        dismissedReview = dismissedReview?.toModel() ?: DismissedReview(),
        rename = rename?.toModel() ?: Rename(),
        commitId = commitId ?: "",
        sha = sha ?: "",
        committer = committer?.toModel() ?: CommitUser(),
        message = message ?: "",
        verification = verification?.toModel() ?: CommitVerification(),
        body = body ?: "",
        reviewRequester = reviewRequester?.toModel() ?: User(),
        requestedReviewer = requestedReviewer?.toModel() ?: User(),
        state = state ?: "",
        user = user?.toModel() ?: User(),
        submittedAt = submittedAt ?: ""
    )
}

data class ApiDismissedReview(
    @field:Json(name = "state") val state: String?,
    @field:Json(name = "review_id") val reviewId: Int?,
    @field:Json(name = "dismissal_message") val dismissalMessage: String?,
    @field:Json(name = "dismissal_commit_id") val dismissalCommitId: String?
) {
    fun toModel() = DismissedReview(
        state = state ?: "",
        reviewId = reviewId ?: 0,
        dismissalMessage = dismissalMessage ?: "",
        dismissalCommitId = dismissalCommitId ?: ""
    )
}

data class ApiRename(
    @field:Json(name = "from") val from: String?,
    @field:Json(name = "to") val to: String?
) {
    fun toModel() = Rename(
        from = from ?: "",
        to = to ?: ""
    )
}