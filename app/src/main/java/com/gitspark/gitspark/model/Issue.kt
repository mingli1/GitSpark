package com.gitspark.gitspark.model

data class Issue(
    val id: Long = 0,
    val number: Int = 0,
    val state: String = "",
    val title: String = "",
    val body: String = "",
    val user: User = User(),
    val labels: List<Label> = emptyList(),
    val assignee: User = User(),
    val assignees: List<User> = emptyList(),
    val locked: Boolean = false,
    val numComments: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val repo: Repo = Repo(),
    val pullRequest: PullRequest = PullRequest()
)

data class Label(
    val name: String = "",
    val description: String = "",
    val color: String = "",
    val default: Boolean = false
)