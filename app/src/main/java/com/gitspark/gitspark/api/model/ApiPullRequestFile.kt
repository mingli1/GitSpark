package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.PullRequestFile
import com.squareup.moshi.Json

data class ApiPullRequestFile(
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "filename") val name: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "additions") val additions: Int?,
    @field:Json(name = "deletions") val deletions: Int?,
    @field:Json(name = "changes") val changes: Int?
) {

    fun toModel() = PullRequestFile(
        sha = sha ?: "",
        name = name ?: "",
        status = status ?: "",
        additions = additions ?: 0,
        deletions = deletions ?: 0,
        changes = changes ?: 0
    )
}