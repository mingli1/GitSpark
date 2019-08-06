package com.gitspark.gitspark.model

data class AuthUser(
    val username: String = "",
    val userId: Int = 0,
    val avatarUrl: String = "",
    val htmlUrl: String = "",
    val type: String = "",
    val siteAdmin: Boolean = false,
    val name: String = "",
    val company: String = "",
    val location: String = "",
    val email: String = "",
    val bio: String = "",
    val numPublicRepos: Int = 0,
    val numPublicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val numPrivateGists: Int = 0,
    val totalPrivateRepos: Int = 0,
    val ownedPrivateRepos: Int = 0,
    val diskUsage: Int = 0,
    val collaborators: Int = 0,
    val plan: GitHubPlan = GitHubPlan()
)