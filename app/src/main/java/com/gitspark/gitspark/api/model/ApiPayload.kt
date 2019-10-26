package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.EventCommit
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.IssueComment
import com.gitspark.gitspark.model.Payload
import com.squareup.moshi.Json

data class ApiPayload(
    @field:Json(name = "action") val action: String?,
    // IssuesEvent
    @field:Json(name = "issue") val issue: ApiIssue?,
    // IssueCommentEvent
    @field:Json(name = "comment") val comment: ApiIssueComment?,
    // PushEvent
    @field:Json(name = "push_id") val pushId: Long?,
    @field:Json(name = "size") val numCommits: Int?,
    @field:Json(name = "distinct_size") val numDistinctCommits: Int?,
    @field:Json(name = "ref") val ref: String?,
    @field:Json(name = "head") val head: String?,
    @field:Json(name = "before") val before: String?,
    @field:Json(name = "commits") val commits: List<ApiEventCommit>?
) {
    fun toModel() = Payload(
        action = action ?: "",
        issue = issue?.toModel() ?: Issue(),
        pushId = pushId ?: 0,
        numCommits = numCommits ?: 0,
        numDistinctCommits = numDistinctCommits ?: 0,
        ref = ref ?: "",
        head = head ?: "",
        before = before ?: "",
        commits = commits?.map { it.toModel() } ?: emptyList(),
        comment = comment?.toModel() ?: IssueComment()
    )
}

data class ApiEventCommit(
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "distinct") val distinct: Boolean?
) {
    fun toModel() = EventCommit(
        sha = sha ?: "",
        message = message ?: "",
        distinct = distinct ?: false
    )
}