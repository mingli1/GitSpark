package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.EventComment
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
) : EventComment {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun createdAt() = createdAt
}