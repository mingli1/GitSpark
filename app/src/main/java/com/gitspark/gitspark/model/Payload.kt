package com.gitspark.gitspark.model

data class Payload(
    // PushEvent
    val pushId: Int = 0,
    val numCommits: Int = 0,
    val numDistinctCommits: Int = 0,
    val ref: String = "",
    val head: String = "",
    val before: String = "",
    val commits: List<Commit> = emptyList()
)