package com.gitspark.gitspark.model

data class GitHubPlan(
    val name: String,
    val space: Int,
    val privateRepos: Int,
    val collaborators: Int
)