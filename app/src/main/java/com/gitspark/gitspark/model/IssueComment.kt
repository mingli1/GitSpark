package com.gitspark.gitspark.model

data class IssueComment(
    val body: String = "",
    val user: User = User(),
    val createdAt: String = "",
    val updatedAt: String = ""
)