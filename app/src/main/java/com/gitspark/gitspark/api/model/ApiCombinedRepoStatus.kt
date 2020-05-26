package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.CombinedRepoStatus
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.RepoStatus
import com.squareup.moshi.Json

data class ApiCombinedRepoStatus(
    @field:Json(name = "state") val state: String?,
    @field:Json(name = "statuses") val statuses: List<ApiRepoStatus>?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "total_count") val totalCount: Int?,
    @field:Json(name = "repository") val repo: ApiRepo?
) {

    fun toModel() = CombinedRepoStatus(
        state = state ?: "",
        statuses = statuses?.map { it.toModel() } ?: emptyList(),
        sha = sha ?: "",
        totalCount = totalCount ?: 0,
        repo = repo?.toModel() ?: Repo()
    )
}

data class ApiRepoStatus(
    @field:Json(name = "id") val id: Long?,
    @field:Json(name = "avatar_url") val avatarUrl: String?,
    @field:Json(name = "state") val state: String?,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "target_url") val targetUrl: String?,
    @field:Json(name = "context") val context: String?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "updated_at") val updatedAt: String?
) {

    fun toModel() = RepoStatus(
        id = id ?: 0L,
        avatarUrl = avatarUrl ?: "",
        state = state ?: "",
        description = description ?: "",
        targetUrl = targetUrl ?: "",
        context = context ?: "",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}