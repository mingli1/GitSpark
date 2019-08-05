package com.gitspark.gitspark.model

data class AuthUser(
    val username: String,
    val userId: Int,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: String,
    val siteAdmin: Boolean,
    val name: String,
    val company: String,
    val location: String,
    val email: String,
    val bio: String,
    val numPublicRepos: Int,
    val numPublicGists: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String,
    val updatedAt: String,
    val numPrivateGists: Int,
    val totalPrivateRepos: Int,
    val ownedPrivateRepos: Int,
    val diskUsage: Int,
    val collaborators: Int,
    val plan: GitHubPlan
)