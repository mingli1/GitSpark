package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json

data class ApiGitHubPlan(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "space") val space: Int,
    @field:Json(name = "private_repos") val numPrivateRepos: Int,
    @field:Json(name = "collaborators") val collaborators: Int
)