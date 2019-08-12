package com.gitspark.gitspark.model

const val PREFERENCES_TOKEN = "PREFERENCES_TOKEN"

data class Token(
    val tokenId: Int = 0,
    val value: String = "",
    val hashedValue: String = "",
    val scopes: List<String> = emptyList(),
    val note: String = ""
)