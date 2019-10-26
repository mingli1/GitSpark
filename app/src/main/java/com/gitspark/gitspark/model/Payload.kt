package com.gitspark.gitspark.model

data class Payload(
    val action: String = "",
    val repo: Repo = Repo(),
    // ForkEvent
    val forkee: Repo = Repo(),
    // IssuesEvent
    val issue: Issue = Issue(),
    // IssueCommentEvent
    val comment: IssueComment = IssueComment(),
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