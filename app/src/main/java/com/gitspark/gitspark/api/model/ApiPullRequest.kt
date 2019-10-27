package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.PRBranch
import com.gitspark.gitspark.model.PullRequest
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiPullRequest(
    @field:Json(name = "number") val number: Int?,
    @field:Json(name = "state") val state: String?,
    @field:Json(name = "locked") val locked: Boolean?,
    @field:Json(name = "title") val title: String?,
    @field:Json(name = "user") val user: ApiUser?,
    @field:Json(name = "body") val body: String?,
    @field:Json(name = "labels") val labels: List<ApiLabel>?,
    // todo milestone
    @field:Json(name = "active_lock_reason") val lockReason: String?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "updated_at") val updatedAt: String?,
    @field:Json(name = "closed_at") val closedAt: String?,
    @field:Json(name = "merged_at") val mergedAt: String?,
    @field:Json(name = "merge_commit_sha") val mergeSha: String?,
    @field:Json(name = "assignee") val assignee: ApiUser?,
    @field:Json(name = "assignees") val assignees: List<ApiUser>?,
    @field:Json(name = "requested_reviewers") val requestedReviewers: List<ApiUser>?,
    @field:Json(name = "head") val head: ApiPRBranch?,
    @field:Json(name = "base") val base: ApiPRBranch?,
    @field:Json(name = "draft") val draft: Boolean?
) {
    fun toModel() = PullRequest(
        number = number ?: 0,
        state = state ?: "",
        locked = locked ?: false,
        title = title ?: "",
        user = user?.toModel() ?: User(),
        body = body ?: "",
        labels = labels?.map { it.toModel() } ?: emptyList(),
        lockReason = lockReason ?: "",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        closedAt = closedAt ?: "",
        mergedAt = mergedAt ?: "",
        mergeSha = mergeSha ?: "",
        assignee = assignee?.toModel() ?: User(),
        assignees = assignees?.map { it.toModel() } ?: emptyList(),
        requestReviewers = requestedReviewers?.map { it.toModel() } ?: emptyList(),
        head = head?.toModel() ?: PRBranch(),
        base = base?.toModel() ?: PRBranch(),
        draft = draft ?: false
    )
}

data class ApiPRBranch(
    @field:Json(name = "label") val label: String?,
    @field:Json(name = "ref") val ref: String?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "user") val user: ApiUser?,
    @field:Json(name = "repo") val repo: ApiRepo?
) {
    fun toModel() = PRBranch(
        label = label ?: "",
        ref = ref ?: "",
        sha = sha ?: "",
        user = user?.toModel() ?: User(),
        repo = repo?.toModel() ?: Repo()
    )
}