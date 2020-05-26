package com.gitspark.gitspark.model

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
)