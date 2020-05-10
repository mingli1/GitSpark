package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class IssueComment(
    val id: Long = 0,
    val htmlUrl: String = "",
    var body: String = "",
    val user: User = User(),
    val createdAt: String = "",
    val updatedAt: String = "",
    val commitId: String = "",
    val association: String = ""
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun areItemsTheSame(other: Pageable) = this == (other as? IssueComment ?: false)

    fun toIssueEvent() = IssueEvent(
        id = id,
        htmlUrl = htmlUrl,
        body = body,
        actor = user,
        createdAt = createdAt,
        commitId = commitId
    )
}