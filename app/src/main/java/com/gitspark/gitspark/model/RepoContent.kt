package com.gitspark.gitspark.model

const val TYPE_FILE = "file"
const val TYPE_DIRECTORY = "dir"

data class RepoContent(
    val type: String = "",
    val encoding: String = "",
    val size: Int = 0,
    val name: String = "",
    val path: String = "",
    val content: String = "",
    val sha: String = "",
    val url: String = "",
    val gitUrl: String = "",
    val htmlUrl: String = "",
    val downloadUrl: String = "",
    val links: RepoLinks = RepoLinks()
)

data class RepoLinks(
    val git: String = "",
    val self: String = "",
    val html: String = ""
)