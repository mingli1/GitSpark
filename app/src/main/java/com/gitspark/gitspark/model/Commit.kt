package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class Commit(
    val url: String = "",
    val sha: String = "",
    val commit: CommitDetail = CommitDetail(),
    val author: User = User(),
    val committer: User = User()
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    fun getDate() = commit.committer.date
}

data class CommitDetail(
    val url: String = "",
    val author: CommitUser = CommitUser(),
    val committer: CommitUser = CommitUser(),
    val message: String = "",
    val commentCount: Int = 0
)

data class CommitUser(
    val name: String = "",
    val email: String = "",
    val date: String = ""
)