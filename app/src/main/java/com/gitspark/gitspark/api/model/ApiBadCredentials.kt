package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json

data class ApiBadCredentials(
    @field:Json(name = "message") val message: String,
    @field:Json(name = "documentation_url") val docUrl: String
)