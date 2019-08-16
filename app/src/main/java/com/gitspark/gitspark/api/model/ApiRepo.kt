package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json

data class ApiRepo(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "full_name") val fullName: String,
    @field:Json(name = "owner") val owner: ApiAuthUser,
    @field:Json(name = "private") val private: Boolean,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "fork") val fork: Boolean,
    @field:Json(name = "language") val language: String,
    @field:Json(name = "forks_count") val numForks: Int,
    @field:Json(name = "stargazers_count") val stars: Int,
    @field:Json(name = "watchers_count") val watches: Int,
    @field:Json(name = "default_branch") val defaultBranch: String,
    @field:Json(name = "open_issues_count") val numOpenIssues: Int,
    @field:Json(name = "topics") val topics: List<String>,
    @field:Json(name = "has_issues") val hasIssues: Boolean,
    @field:Json(name = "has_projects") val hasProjects: Boolean,
    @field:Json(name = "has_wiki") val hasWiki: Boolean,
    @field:Json(name = "has_pages") val hasPages: Boolean,
    @field:Json(name = "has_downloads") val hasDownloads: Boolean,
    @field:Json(name = "archived") val archived: Boolean,
    @field:Json(name = "disabled") val disabled: Boolean,
    @field:Json(name = "pushed_at") val pushedAt: String,
    @field:Json(name = "created_at") val createdAt: String,
    @field:Json(name = "updated_at") val updatedAt: String,
    @field:Json(name = "permissions") val permissions: RepoPermissions,
    @field:Json(name = "license") val license: RepoLicense
)

data class RepoPermissions(
    @field:Json(name = "admin") val admin: Boolean,
    @field:Json(name = "push") val push: Boolean,
    @field:Json(name = "pull") val pull: Boolean
)

data class RepoLicense(
    @field:Json(name = "key") val key: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "spdx_id") val spdxId: String,
    @field:Json(name = "url") val url: String
)