package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.IssueComment
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiIssueComment(
    @field:Json(name = "id") val id: Long?,
    @field:Json(name = "html_url") val htmlUrl: String?,
    @field:Json(name = "body") val body: String?,
    @field:Json(name = "user") val user: ApiUser?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "updated_at") val updatedAt: String?,
    @field:Json(name = "commit_id") val commitId: String?,
    @field:Json(name = "author_association") val association: String?
) {
    fun toModel() = IssueComment(
        id = id ?: 0,
        htmlUrl = htmlUrl ?: "",
        body = body ?: "",
        user = user?.toModel() ?: User(),
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        commitId = commitId ?: "",
        association = association ?: ""
    )
}

data class ApiIssueCommentRequest(
    @field:Json(name = "body") val body: String
)