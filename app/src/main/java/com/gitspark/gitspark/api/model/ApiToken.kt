package com.gitspark.gitspark.api.model

import com.gitspark.gitspark.model.Token
import com.squareup.moshi.Json

data class ApiToken(
    @field:Json(name = "id") val tokenId: Int,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "scopes") val scopes: List<String>,
    @field:Json(name = "token") val token: String,
    @field:Json(name = "token_last_eight") val tokenLastEight: String,
    @field:Json(name = "hashed_token") val hashedToken: String,
    @field:Json(name = "updated_at") val updatedDate: String,
    @field:Json(name = "created_at") val createdDate: String,
    @field:Json(name = "note") val note: String
) {
    fun toModel() = Token(tokenId, token, hashedToken, scopes, note)
}