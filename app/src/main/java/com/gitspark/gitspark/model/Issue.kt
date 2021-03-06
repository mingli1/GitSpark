package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class Issue(
    val id: Long = 0,
    val url: String = "",
    val htmlUrl: String = "",
    val repoUrl: String = "",
    val number: Int = 0,
    val state: String = "",
    val title: String = "",
    val body: String = "",
    val user: User = User(),
    val labels: List<Label> = emptyList(),
    val assignee: User = User(),
    val assignees: List<User> = emptyList(),
    val locked: Boolean = false,
    val numComments: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val closedAt: String = "",
    val repo: Repo = Repo(),
    val pullRequest: PullRequest = PullRequest()
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun areItemsTheSame(other: Pageable) = this == (other as? Issue ?: false)

    fun getRepoFullNameFromUrl(): String {
        if (repoUrl.isEmpty()) return ""
        val split = repoUrl.split("/")
        return "${split[split.size - 2]}/${split.last()}"
    }
}

data class Label(
    val name: String = "",
    val description: String = "",
    val color: String = "",
    val default: Boolean = false
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun areItemsTheSame(other: Pageable) = this == (other as? Label ?: false)
}