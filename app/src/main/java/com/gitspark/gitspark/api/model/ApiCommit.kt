package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.*
import com.squareup.moshi.Json

data class ApiCommit(
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "commit") val commit: ApiCommitDetail?,
    @field:Json(name = "author") val author: ApiUser?,
    @field:Json(name = "committer") val committer: ApiUser?,
    @field:Json(name = "repository") val repo: ApiRepo?
) {
    fun toModel() = Commit(
        url = url ?: "",
        sha = sha ?: "",
        commit = commit?.toModel() ?: CommitDetail(),
        author = author?.toModel() ?: User(),
        committer = committer?.toModel() ?: User(),
        repo = repo?.toModel() ?: Repo()
    )
}

data class ApiCommitDetail(
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "author") val author: ApiCommitUser?,
    @field:Json(name = "committer") val committer: ApiCommitUser?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "comment_count") val commentCount: Int?,
    @field:Json(name = "verification") val verification: ApiCommitVerification?
) {
    fun toModel() = CommitDetail(
        url = url ?: "",
        author = author?.toModel() ?: CommitUser(),
        committer = committer?.toModel() ?: CommitUser(),
        message = message ?: "",
        commentCount = commentCount ?: 0,
        verification = verification?.toModel() ?: CommitVerification()
    )
}

data class ApiCommitUser(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "date") val date: String?
) {
    fun toModel() = CommitUser(
        name = name ?: "",
        email = email ?: "",
        date = date ?: ""
    )
}

data class ApiCommitVerification(
    @field:Json(name = "verified") val verified: Boolean?,
    @field:Json(name = "reason") val reason: String?,
    @field:Json(name = "signature") val signature: String?,
    @field:Json(name = "payload") val payload: String?
) {
    fun toModel() = CommitVerification(
        verified = verified ?: false,
        reason = reason ?: "",
        signature = signature ?: "",
        payload = payload ?: ""
    )
}