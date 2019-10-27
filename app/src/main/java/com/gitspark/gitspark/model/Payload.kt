package com.gitspark.gitspark.model

data class Payload(
    // shared
    val action: String = "",
    val repo: Repo = Repo(),
    val ref: String = "",
    // CreateEvent
    val refType: String = "",
    val masterBranch: String = "",
    val description: String = "",
    // ForkEvent
    val forkee: Repo = Repo(),
    // IssuesEvent
    val issue: Issue = Issue(),
    // IssueCommentEvent
    val comment: IssueComment = IssueComment(),
    // PullRequestEvent
    val number: Int = 0,
    val pullRequest: PullRequest = PullRequest(),
    // PushEvent
    val pushId: Long = 0,
    val numCommits: Int = 0,
    val numDistinctCommits: Int = 0,
    val head: String = "",
    val before: String = "",
    val commits: List<EventCommit> = emptyList()
)

data class EventCommit(
    val sha: String = "",
    val message: String = "",
    val distinct: Boolean = false
)