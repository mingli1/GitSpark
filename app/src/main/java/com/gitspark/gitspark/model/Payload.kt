package com.gitspark.gitspark.model

data class Payload(
    // PushEvent
    val pushId: Long = 0,
    val numCommits: Int = 0,
    val numDistinctCommits: Int = 0,
    val ref: String = "",
    val head: String = "",
    val before: String = "",
    val commits: List<EventCommit> = emptyList()
)

data class EventCommit(
    val sha: String = "",
    val message: String = "",
    val distinct: Boolean = false
)