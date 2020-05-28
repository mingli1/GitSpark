package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

const val STATUS_SUCCESS = "success"
const val STATUS_PENDING = "pending"
const val STATUS_FAILED = "failure"
const val STATUS_ERROR = "error"

data class CombinedRepoStatus(
    val state: String = "",
    val statuses: List<RepoStatus> = emptyList(),
    val sha: String = "",
    val totalCount: Int = 0,
    val repo: Repo = Repo()
)

data class RepoStatus(
    val id: Long = 0L,
    val avatarUrl: String = "",
    val state: String = "",
    val description: String = "",
    val targetUrl: String = "",
    val context: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
) : Pageable {

    override fun areItemsTheSame(other: Pageable) = this == (other as? RepoStatus ?: false)

    override fun getViewType() = VIEW_TYPE_VIEW
}