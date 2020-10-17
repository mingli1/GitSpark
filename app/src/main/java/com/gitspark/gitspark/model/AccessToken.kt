package com.gitspark.gitspark.model

const val PREFERENCES_TOKEN = "PREFERENCES_TOKEN"

data class AccessToken(
    val token: String = "",
    val scopes: List<String> = emptyList(),
    val tokenType: String = ""
)