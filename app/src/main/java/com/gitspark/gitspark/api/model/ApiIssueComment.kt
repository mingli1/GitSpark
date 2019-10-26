package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.IssueComment
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiIssueComment(
    @field:Json(name = "body") val body: String?,
    @field:Json(name = "user") val user: ApiUser?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "updated_at") val updatedAt: String?
) {
    fun toModel() = IssueComment(
        body = body ?: "",
        user = user?.toModel() ?: User(),
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}