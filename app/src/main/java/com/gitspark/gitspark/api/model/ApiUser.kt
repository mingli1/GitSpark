package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.User
import com.squareup.moshi.Json

data class ApiUser(
    @field:Json(name = "login") val username: String?,
    @field:Json(name = "id") val userId: Long?,
    @field:Json(name = "avatar_url") val avatarUrl: String?,
    @field:Json(name = "url") val url: String?,
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "site_admin") val siteAdmin: Boolean?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "company") val company: String?,
    @field:Json(name = "blog") val blogUrl: String?,
    @field:Json(name = "location") val location: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "bio") val bio: String?,
    @field:Json(name = "public_repos") val numPublicRepos: Int?,
    @field:Json(name = "public_gists") val numPublicGists: Int?,
    @field:Json(name = "followers") val followers: Int?,
    @field:Json(name = "following") val following: Int?,
    @field:Json(name = "created_at") val createdAt: String?,
    @field:Json(name = "updated_at") val updatedAt: String?,
    @field:Json(name = "hireable") val hireable: Boolean?,
    @field:Json(name = "contributions") val contributions: Int?
) {

    fun toModel() = User().also {
        it.login = username ?: ""
        it.userId = userId ?: 0
        it.avatarUrl = avatarUrl ?: ""
        it.type = type ?: ""
        it.siteAdmin = siteAdmin ?: false
        it.name = name ?: ""
        it.company = company ?: ""
        it.location = location ?: ""
        it.email = email ?: ""
        it.bio = bio ?: ""
        it.blogUrl = blogUrl ?: ""
        it.numPublicRepos = numPublicRepos ?: 0
        it.numPublicGists = numPublicGists ?: 0
        it.followers = followers ?: 0
        it.following = following ?: 0
        it.createdAt = createdAt ?: ""
        it.updatedAt = updatedAt ?: ""
        it.hireable = hireable ?: false
        it.contributions = contributions ?: 0
    }
}