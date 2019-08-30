package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json

data class ApiEditProfileRequest(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "blog") val url: String,
    @field:Json(name = "company") val company: String,
    @field:Json(name = "location") val location: String,
    @field:Json(name = "hireable") val hireable: Boolean,
    @field:Json(name = "bio") val bio: String
)