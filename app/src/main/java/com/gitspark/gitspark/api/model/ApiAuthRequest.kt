package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.BuildConfig.*
import com.squareup.moshi.Json

val DEFAULT_AUTH = ApiAuthRequest(
    scopes = listOf("user", "repo", "gist", "notifications"),
    note = APPLICATION_ID,
    noteUrl = CALLBACK_URL,
    clientId = GITHUB_CLIENT_ID,
    clientSecret = GITHUB_CLIENT_SECRET
)

data class ApiAuthRequest(
    @Json(name = "scopes") val scopes: List<String>,
    @Json(name = "note") val note: String,
    @Json(name = "note_url") val noteUrl: String,
    @Json(name = "client_id") val clientId: String,
    @Json(name = "client_secret") val clientSecret: String
)