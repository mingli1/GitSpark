package com.gitspark.gitspark.model

data class App(
    val id: Long = 0L,
    val slug: String = "",
    val owner: User = User(),
    val name: String = "",
    val description: String = "",
    val createdAt: String = ""
)