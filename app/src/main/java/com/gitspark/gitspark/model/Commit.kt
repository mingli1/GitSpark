package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class Commit(
    val url: String = "",
    val sha: String = "",
    val commit: CommitDetail = CommitDetail(),
    val author: User = User(),
    val committer: User = User(),
    val repo: Repo = Repo()
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun areItemsTheSame(other: Pageable) = this == (other as? Commit ?: false)

    fun getDate() = commit.committer.date
}

data class CommitDetail(
    val url: String = "",
    val author: CommitUser = CommitUser(),
    val committer: CommitUser = CommitUser(),
    val message: String = "",
    val commentCount: Int = 0,
    val verification: CommitVerification = CommitVerification()
)

data class CommitUser(
    val name: String = "",
    val email: String = "",
    val date: String = ""
)

data class CommitVerification(
    val verified: Boolean = false,
    val reason: String = "",
    val signature: String = "",
    val payload: String = ""
)