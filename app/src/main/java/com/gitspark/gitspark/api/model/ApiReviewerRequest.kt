package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json

data class ApiReviewerRequest(
    @field:Json(name = "reviewers") val reviewers: List<String>,
    @field:Json(name = "team_reviewers") val teamReviewers: List<String> = emptyList()
)