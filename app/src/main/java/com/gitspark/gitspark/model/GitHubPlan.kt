package com.gitspark.gitspark.model

data class GitHubPlan(
    val planName: String = "",
    val planSpace: Int = 0,
    val planPrivateRepos: Int = 0,
    val planCollaborators: Int = 0
)