package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Payload
import com.squareup.moshi.Json

data class ApiPayload(
    // PushEvent
    @field:Json(name = "push_id") val pushId: Int?,
    @field:Json(name = "size") val numCommits: Int?,
    @field:Json(name = "distinct_size") val numDistinctCommits: Int?,
    @field:Json(name = "ref") val ref: String?,
    @field:Json(name = "head") val head: String?,
    @field:Json(name = "before") val before: String?,
    @field:Json(name = "commits") val commits: List<ApiCommit>?
) {
    fun toModel() = Payload(
        pushId = pushId ?: 0,
        numCommits = numCommits ?: 0,
        numDistinctCommits = numDistinctCommits ?: 0,
        ref = ref ?: "",
        head = head ?: "",
        before = before ?: "",
        commits = commits?.map { it.toModel() } ?: emptyList()
    )
}