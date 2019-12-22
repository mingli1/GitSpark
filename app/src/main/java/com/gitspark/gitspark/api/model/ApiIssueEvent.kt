package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.DismissedReview
import com.gitspark.gitspark.model.IssueEvent
import com.gitspark.gitspark.model.Rename
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiIssueEvent(
    @field:Json(name = "id") val id: Long?,
    @field:Json(name = "actor") val actor: ApiUser?,
    @field:Json(name = "event") val event: String?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "assignee") val assignee: ApiUser?,
    @field:Json(name = "assignees") val assignees: List<ApiUser>?,
    @field:Json(name = "assigner") val assigner: ApiUser?,
    @field:Json(name = "labels") val labels: List<ApiLabel>?,
    @field:Json(name = "dismissed_review") val dismissedReview: ApiDismissedReview?,
    @field:Json(name = "rename") val rename: ApiRename?,
    @field:Json(name = "commit_id") val commitId: String?
) {
    fun toModel() = IssueEvent(
        id = id ?: 0,
        actor = actor?.toModel() ?: User(),
        event = event ?: "",
        createdAt = createdAt ?: "",
        assignee = assignee?.toModel() ?: User(),
        assignees = assignees?.map { it.toModel() } ?: emptyList(),
        assigner = assigner?.toModel() ?: User(),
        labels = labels?.map { it.toModel() } ?: emptyList(),
        dismissedReview = dismissedReview?.toModel() ?: DismissedReview(),
        rename = rename?.toModel() ?: Rename(),
        commitId = commitId ?: ""
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