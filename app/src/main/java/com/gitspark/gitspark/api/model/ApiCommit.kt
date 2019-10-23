package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.model.CommitDetail
import com.gitspark.gitspark.model.CommitUser
import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiCommit(
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "sha") val sha: String?,
    @field:Json(name = "commit") val commit: ApiCommitDetail?,
    @field:Json(name = "author") val author: ApiUser?,
    @field:Json(name = "committer") val committer: ApiUser?
) {
    fun toModel() = Commit(
        url = url ?: "",
        sha = sha ?: "",
        commit = commit?.toModel() ?: CommitDetail(),
        author = author?.toModel() ?: User(),
        committer = committer?.toModel() ?: User()
    )
}

data class ApiCommitDetail(
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "author") val author: ApiCommitUser?,
    @field:Json(name = "committer") val committer: ApiCommitUser?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "comment_count") val commentCount: Int?
) {
    fun toModel() = CommitDetail(
        url = url ?: "",
        author = author?.toModel() ?: CommitUser(),
        committer = committer?.toModel() ?: CommitUser(),
        message = message ?: "",
        commentCount = commentCount ?: 0
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