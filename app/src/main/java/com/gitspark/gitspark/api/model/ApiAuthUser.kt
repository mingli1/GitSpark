package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.AuthUser
import com.squareup.moshi.Json

data class ApiAuthUser(
    @field:Json(name = "login") val username: String,
    @field:Json(name = "id") val userId: Int,
    @field:Json(name = "node_id") val nodeId: String,
    @field:Json(name = "avatar_url") val avatarUrl: String,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "html_url") val htmlUrl: String,
    @field:Json(name = "followers_url") val followersUrl: String,
    @field:Json(name = "following_url") val followingUrl: String,
    @field:Json(name = "gists_url") val gistsUrl: String,
    @field:Json(name = "starred_url") val starredUrl: String,
    @field:Json(name = "subscriptions_url") val subscriptionsUrl: String,
    @field:Json(name = "organizations_url") val organizationsUrl: String,
    @field:Json(name = "repos_url") val reposUrl: String,
    @field:Json(name = "events_url") val eventsUrl: String,
    @field:Json(name = "received_events_url") val receivedEventsUrl: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "site_admin") val siteAdmin: Boolean,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "company") val company: String,
    @field:Json(name = "blog") val blogUrl: String,
    @field:Json(name = "location") val location: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "hireable") val hireable: Boolean,
    @field:Json(name = "bio") val bio: String,
    @field:Json(name = "public_repos") val numPublicRepos: Int,
    @field:Json(name = "public_gists") val numPublicGists: Int,
    @field:Json(name = "followers") val followers: Int,
    @field:Json(name = "following") val following: Int,
    @field:Json(name = "created_at") val createdAt: String,
    @field:Json(name = "updated_at") val updatedAt: String,
    @field:Json(name = "private_gists") val numPrivateGists: Int,
    @field:Json(name = "total_private_repos") val totalPrivateRepos: Int,
    @field:Json(name = "owned_private_repos") val ownedPrivateRepos: Int,
    @field:Json(name = "disk_usage") val diskUsage: Int,
    @field:Json(name = "collaborators") val collaborators: Int,
    @field:Json(name = "two_factor_authentication") val twoFactorAuth: Boolean,
    @field:Json(name = "plan") val plan: ApiGitHubPlan
) {
    fun toModel() = AuthUser().also {
        it.login = username
        it.userId = userId
        it.avatarUrl = avatarUrl
        it.htmlUrl = htmlUrl
        it.type = type
        it.siteAdmin = siteAdmin
        it.name = name
        it.company = company
        it.location = location
        it.email = email
        it.bio = bio
        it.numPublicRepos = numPublicRepos
        it.numPublicGists = numPublicGists
        it.followers = followers
        it.following = following
        it.createdAt = createdAt
        it.updatedAt = updatedAt
        it.numPrivateGists = numPrivateGists
        it.totalPrivateRepos = totalPrivateRepos
        it.ownedPrivateRepos = ownedPrivateRepos
        it.diskUsage = diskUsage
        it.collaborators = collaborators
        it.plan = plan.toModel()
    }
}