package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.AccessToken
import com.squareup.moshi.Json

data class ApiAccessToken(
    @field:Json(name = "access_token") val token: String?,
    @field:Json(name = "scope") val scope: String?,
    @field:Json(name = "token_type") val tokenType: String?
) {

    fun toModel() = AccessToken(
        token = token ?: "",
        scopes = scope?.split(",") ?: emptyList(),
        tokenType = tokenType ?: ""
    )
}