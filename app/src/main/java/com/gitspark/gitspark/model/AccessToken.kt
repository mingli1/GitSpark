package com.gitspark.gitspark.model

data class AccessToken(
    val token: String = "",
    val scopes: List<String> = emptyList(),
    val tokenType: String = ""
)