package com.gitspark.gitspark.models

data class Authorization(
    val scopes: List<String> = emptyList(),
    val note: String = "",
    val noteUrl: String = "",
    val clientId: String = "",
    val clientSecret: String = "",
    val fingerprint: String = ""
)