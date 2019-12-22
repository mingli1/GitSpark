package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class IssueComment(
    val body: String = "",
    val user: User = User(),
    val createdAt: String = "",
    val updatedAt: String = "",
    val commitId: String = "",
    val association: String = ""
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW
}