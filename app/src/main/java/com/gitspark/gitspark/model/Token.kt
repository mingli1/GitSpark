package com.gitspark.gitspark.model

data class Token(
    val value: String,
    val scopes: List<String>
)