package com.gitspark.gitspark.model

data class File(
    val name: String = "",
    val path: String = "",
    val sha: String = "",
    val url: String = "",
    val repo: Repo = Repo()
)