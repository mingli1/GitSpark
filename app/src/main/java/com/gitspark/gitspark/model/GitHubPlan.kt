package com.gitspark.gitspark.model

data class GitHubPlan(
    val name: String = "",
    val space: Int = 0,
    val privateRepos: Int = 0,
    val collaborators: Int = 0
)