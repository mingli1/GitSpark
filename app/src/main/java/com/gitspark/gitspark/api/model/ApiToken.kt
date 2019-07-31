package com.gitspark.gitspark.api.model

import com.squareup.moshi.Json
import java.util.Date

data class ApiToken(
    @Json(name = "id") val id: Int,
    @Json(name = "url") val url: String,
    @Json(name = "scopes") val scopes: List<String>,
    @Json(name = "token") val token: String,
    @Json(name = "token_last_eight") val tokenLastEight: String,
    @Json(name = "hashed_token") val hashedToken: String,
    @Json(name = "updated_at") val updatedDate: Date,
    @Json(name = "created_at") val createdDate: Date
)