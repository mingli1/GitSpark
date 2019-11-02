package com.gitspark.gitspark.model

import com.gitspark.gitspark.api.model.RepoLicense
import com.gitspark.gitspark.api.model.RepoPermissions
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

data class Repo(
    val repoId: Long = 0,
    val repoName: String = "",
    val fullName: String = "",
    val owner: User = User(),
    val isPrivate: Boolean = false,
    val repoDescription: String = "",
    val isForked: Boolean = false,
    val repoLanguage: String = "",
    val numForks: Int = 0,
    val numStars: Int = 0,
    val numWatches: Int = 0,
    val defaultBranch: String = "",
    val numOpenIssues: Int = 0,
    val topics: List<String> = emptyList(),
    val hasIssues: Boolean = false,
    val hasProjects: Boolean = false,
    val hasWiki: Boolean = false,
    val hasPages: Boolean = false,
    val hasDownloads: Boolean = false,
    val archived: Boolean = false,
    val disabled: Boolean = false,
    val repoPushedAt: String = "",
    val repoCreatedAt: String = "",
    val repoUpdatedAt: String = "",
    val permissions: RepoPermissions = RepoPermissions(
        admin = false,
        push = false,
        pull = false
    ),
    val license: RepoLicense = RepoLicense("", "", "", ""),
    var timestamp: String = "",
    var starredAt: String = "",
    var starred: Boolean = false
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW
}