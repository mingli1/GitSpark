package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.BuildConfig.*
import com.squareup.moshi.Json

val DEFAULT_AUTH = ApiAuthRequest(
    scopes = listOf("user", "repo", "gist", "notifications"),
    note = APPLICATION_ID,
    noteUrl = CALLBACK_URL,
    clientSecret = GITHUB_CLIENT_SECRET
)

data class ApiAuthRequest(
    @field:Json(name = "scopes") val scopes: List<String>,
    @field:Json(name = "note") val note: String,
    @field:Json(name = "note_url") val noteUrl: String,
    @field:Json(name = "client_secret") val clientSecret: String
)