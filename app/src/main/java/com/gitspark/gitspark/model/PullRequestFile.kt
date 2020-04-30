package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

const val PR_FILE_ADDED = "added"
const val PR_FILE_REMOVED = "removed"
const val PR_FILE_MODIFIED = "modified"

data class PullRequestFile(
    val sha: String = "",
    val name: String = "",
    val status: String = "",
    val additions: Int = 0,
    val deletions: Int = 0,
    val changes: Int = 0
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun areItemsTheSame(other: Pageable) = this == (other as? PullRequestFile ?: false)
}